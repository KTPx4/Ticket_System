const AccountModel = require('../models/AccountModel')
const EventModel = require('../models/EventModel')

const jwt = require("jsonwebtoken");
const fs = require("fs");
const bcrypt = require("bcrypt");
const UploadIMG = require("./Upload");
const StaffModel = require("../models/StaffModel");
const SECRET_LOGIN = process.env.KEY_SECRET_LOGIN || 'px4-secret-key-login-app'

module.exports.GetMyAccount= async (req, res)=>{
    try{
        var {User} = req.vars
        var acc = await AccountModel.findOne({_id: User._id})
            .populate('follows.event')
            .populate('follows.artist')
            .populate('hisEvent.event')
            .populate('notify.news')
        // .populate('notify.news');      // Lấy thông tin của news

        return res.status(200).json({
            message: "Lấy thông tin thành công",
            data: acc
        })
    }
    catch (e)
    {
        console.log("Error at AccountController - GetMyAccount: ", e)
        return res.status(500).json({
            message: "Lấy thông tin thất bại, thử lại sau"
        })
    }

}

module.exports.Register = async(req, res)=>{
    let { email, pass} = req.body
    let {root} = req.vars
    email = email.toLowerCase()
    var account = null
    try{
        bcrypt.hash(pass, 10)
            .then(async(hashed) =>{

                const Account = await AccountModel.create({
                    name: email,
                    email: email,
                    pass: hashed
                })

                account = Account

                createFolder(root, Account._id, Account.image) ? null : console.log(`Can't create folder for Account: '${Account._id}' - '${Account.user}'`);

                var data = {
                    name: account.name,
                    email: account.email,
                    image: account.image,
                    pass: account.pass
                }

                await jwt.sign(data, SECRET_LOGIN, {expiresIn: "7d"}, (err, tokenLogin)=>{
                    if(err) throw err
                    return res.status(200).json({
                        status: "Login success",
                        message: "Đăng ký thành công",
                        data: tokenLogin
                    })
                })

                // return res.status(201).json({
                //     message: 'Register Success',
                //     data: {
                //         account: account
                //     }
                // })
            })


    }
    catch(err)
    {
        console.log("Error At AccountController - Register: ", err);
        return res.status(500).json({
            message: 'Error At Server',
            data: {}
        })
    }
}
module.exports.ChangePass = async(req,res)=>{
    var {oldPass, newPass} = req.body
    var {User} = req.vars
    try{
        bcrypt.hash(newPass, 10)
            .then(async(hashed) =>{

                const Account = await AccountModel.findOneAndUpdate({_id: User._id}, {pass: hashed}, {new: true})

                account = Account

                var data = {
                    name: account.name,
                    email: account.email,
                    image: account.image,
                    pass: account.pass
                }

                await jwt.sign(data, SECRET_LOGIN, {expiresIn: "7d"}, (err, tokenLogin)=>{
                    if(err) throw err
                    return res.status(200).json({
                        status: "Login success",
                        message: "Đổi mật khẩu thành công",
                        data: tokenLogin
                    })
                })

                // return res.status(201).json({
                //     message: 'Register Success',
                //     data: {
                //         account: account
                //     }
                // })
            })


    }
    catch(err)
    {
        console.log("Error At AccountController - Register: ", err);
        return res.status(500).json({
            message: 'Error At Server',
            data: {}
        })
    }
}
module.exports.Login = async (req, res)=>{

    let {root, User} = req.vars
    var data = {
        name: User.name,
        email: User.email,
        image: User.image,
        pass: User.pass
    }

    createFolder(root, User._id, User.image) ? null : console.log(`Can't create folder for Account: '${Staff._id}' - '${Staff.user}'`);

    await jwt.sign(data, SECRET_LOGIN, {expiresIn: "7d"}, (err, tokenLogin)=>{
        if(err) throw err
        return res.status(200).json({
            status: "Login success",
            message: "Đăng nhập thành công",
            data: tokenLogin
        })
    })
}

module.exports.UpdateImage = async(req, res) =>{
    try
    {
        // console.log("1 request");
        let {root}  = req.vars
        var Account = req.vars.User

        const currentPath = `${root}/public/account/${Account._id}`

        let newNameAVT = await UploadIMG.uploadSingle(req.file, currentPath,  Account._id)

        var newAccount = await AccountModel.findOneAndUpdate({_id: Account._id}, {
            image: `${newNameAVT}`
        }, {new: true})

        return res.status(200).json({
            message: 'Thay đổi thông tin thành công',
            data: newAccount
        })
    }
    catch(err)
    {
        console.log("Error At Account Contronller - UpdateProfile: ", err.message);
        return res.status(500).json({
            status: "Error Server When Edit",
            message: "Server đang bận. Vui lòng  thử lại sau!"
        })
    }
}

module.exports.FollowEvent = async(req, res)=>{
    try {
        const { User, Event } = req.vars;


        // Kiểm tra nếu account đã follow event trước đó
        const isAlreadyFollowing = Event.followers.includes(User._id);
        if (!isAlreadyFollowing) {
            // Push account id vào followers của event
            Event.followers.push(User._id);
            await Event.save();
            // return res.status(400).json({ message: "Bạn đã theo dõi sự kiện này rồi" });
        }

        const isAFollow = User.follows.event.includes(Event._id);
        if(!isAFollow)
        {
            // Push event id vào follows.event của account
            User.follows.event.push(Event._id);
            await User.save();
        }

        res.status(200).json({ message: "Theo dõi sự kiện thành công", data: Event._id });

    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Server error", error });
    }


}

module.exports.UnFollowEvent = async(req, res)=>{
    try {
        const { User, Event } = req.vars;


        // Kiểm tra nếu account đã follow event trước đó
        const isAlreadyFollowing = Event.followers.includes(User._id);
        if (isAlreadyFollowing) {
            // Xóa account id khỏi followers của event
            Event.followers = Event.followers.filter(
                (followerId) => !followerId.equals(User._id)
            );
            await Event.save();
        }

        const isAFollow = User.follows.event.includes(Event._id);
        if(isAFollow)
        {
            // Xóa event id khỏi follows.event của account
            User.follows.event = User.follows.event.filter(
                (eventId) => !eventId.equals(Event._id)
            );
            await User.save();
        }

        res.status(200).json({ message: "Bỏ dõi sự kiện thành công", data: Event._id });

    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Server error", error });
    }


}

module.exports.Update = async(req, res)=>{
    var {updateData, User}= req.vars // get update from validator
    var id = User._id

    // Cập nhật tài liệu
    // Nếu không có trường hợp lệ nào để cập nhật
    if (Object.keys(updateData).length === 0) {
        return res.status(400).json({ message: 'Không có giá trị để sửa' });
    }
    const updatedEvent = await AccountModel.findByIdAndUpdate(
        id,
        { $set: updateData }, // Cập nhật chỉ các trường hợp lệ
        { new: true, runValidators: true } // Trả về dữ liệu sau khi cập nhật và kiểm tra tính hợp lệ
    );

    res.status(200).json({
        message: "Chỉnh sửa sự kiện thành công",
        data: updatedEvent
    });
}

const createFolder = (root, idUser, nameAvt)=>
{

    const ROOT_AVT = root + "/public/account"

    let defaultAvt = `${ROOT_AVT}/default.png`

    let folderAccount = ROOT_AVT + "/" + idUser

    let UserAvt = `${ROOT_AVT}/${idUser}/${nameAvt}`

    try
    {
        if (!fs.existsSync(folderAccount))
        {
            fs.mkdirSync(folderAccount);
        }

        if(!fs.existsSync(UserAvt) && fs.existsSync(defaultAvt))
        {
            // console.log("ok, ", UserAvt);
            fs.copyFileSync(defaultAvt, UserAvt)
        }
    }
    catch(err)
    {
        console.log("Error at AccounController - Create Folder Img For User: ", err);
        return false;
    }
    return true;
    // Kiểm tra xem tệp tin nguồn tồn tại hay không
}
