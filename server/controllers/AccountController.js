const AccountModel = require('../models/AccountModel')
const EventModel = require('../models/EventModel')
const TicketModel = require('../models/TicketModel')
const mongoose = require('mongoose')

const sendEmail = require('../modules/Mailer')
const jwt = require("jsonwebtoken");
const fs = require("fs");
const bcrypt = require("bcrypt");
const UploadIMG = require("./Upload");
const StaffModel = require("../models/StaffModel");
const SECRET_LOGIN = process.env.KEY_SECRET_LOGIN || 'px4-secret-key-login-app'
const JWT_SECRET = process.env.JWT_SECRET || "jwt_secret_reset"; // Khóa bí mật cho JWT
const SERVER = process.env.SERVER
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

module.exports.SendReset = async(req, res)=>{

    try{
        var Account = req.vars.User
        var email = Account.email
        // Tạo mật khẩu tạm thời
        const tempPassword = Math.random().toString(36).slice(-8); // Random password (8 ký tự)
        // Tạo JWT
        const token = jwt.sign(
            { email, tempPassword },
            JWT_SECRET,
            { expiresIn: "5m" } // Token hết hạn sau 15 phút
        );
        // Tạo URL khôi phục
        const resetURL = `${SERVER}/api/v1/account/reset?token=${token}`;

        // Gửi email khôi phục
        const emailSubject = "Khôi phục mật khẩu";
        const emailBody = `
            <p>Chào bạn,</p>
            <p>Chúng tôi đã nhận được yêu cầu khôi phục mật khẩu cho tài khoản của bạn.</p>
            <p>Mật khẩu tạm thời của bạn: <strong>${tempPassword}</strong></p>
            <p>Nhấn vào đường dẫn dưới đây để xác nhận và thiết lập mật khẩu mới:</p>
            <a href="${resetURL}" target="_blank" style="color: blue; text-decoration: underline;">Tại đây</a>
            <p>Liên kết này sẽ hết hạn sau 5 phút.</p>
        `;

        await sendEmail(email, emailSubject, emailBody);

        res.status(200).json({
            message: "Email khôi phục mật khẩu đã được gửi.",
        });
    }
    catch (error) {
        console.error("Error in SendReset:", error);
        res.status(500).json({
            message: "Có lỗi xảy ra. Vui lòng thử lại sau.",
        });
    }
}
module.exports.GetReset = async(req, res)=>{
    var {token} = req.query
    try {
        // Giải mã JWT
        const decoded = jwt.verify(token, JWT_SECRET);

        const { email, tempPassword } = decoded;

        // Tìm tài khoản theo email
        const account = await AccountModel.findOne({ email });
        if (!account) {
            return res.status(404).json({ message: "Tài khoản không tồn tại." });
        }

        bcrypt.hash(tempPassword, 10)
            .then(async(hashed) =>{
                account.pass = hashed
                await account.save();
            })

        res.status(200).json({
            message: "Mật khẩu đã được cập nhật thành công.",
        });

    } catch (error) {
        console.error("Error in ResetPassword:", error);

        if (error.name === "TokenExpiredError") {
            return res.status(400).json({ message: "Liên kết khôi phục đã hết hạn." });
        }

        res.status(500).json({
            message: "Có lỗi xảy ra. Vui lòng thử lại sau.",
        });
    }
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

module.exports.GetMyTicket = async (req, res) => {
    try {
        var { User } = req.vars;

        const tickets = await TicketModel.aggregate([
            {
                $match: { accBuy: User._id } // Lọc các ticket theo `accBuy`
            },
            {
                $lookup: { // Join với collection `events`
                    from: 'events',
                    localField: 'event',
                    foreignField: '_id',
                    as: 'eventDetails'
                }
            },
            {
                $unwind: '$eventDetails' // Giải nén mảng `eventDetails` thành object
            },
            {
                $lookup: { // Thêm lookup để lấy thông tin từ `ticket_types`
                    from: 'ticket_types',  // Tên collection chứa thông tin ticket loại vé
                    localField: 'info',  // Trường info chứa ID của loại vé
                    foreignField: '_id',
                    as: 'ticketInfo'  // Kết quả sẽ lưu vào trường `ticketInfo`
                }
            },
            {
                $unwind: { // Giải nén mảng `ticketInfo` thành object
                    path: '$ticketInfo',
                    preserveNullAndEmptyArrays: true // Đảm bảo ticketInfo vẫn là null nếu không có dữ liệu
                }
            },
            {
                $group: { // Group theo sự kiện
                    _id: '$event', // Group theo event ID
                    eventDetails: { $first: '$eventDetails' }, // Lấy thông tin sự kiện đầu tiên
                    tickets: { $push: '$$ROOT' } // Đưa tất cả các ticket vào mảng
                }
            },
            {
                $project: { // Định dạng lại kết quả
                    _id: 0, // Không hiển thị `_id` trong kết quả
                    event: '$eventDetails',  // Thông tin sự kiện
                    tickets: 1 // Các vé trong sự kiện
                }
            }
        ]);

        return res.status(200).json({
            message: "Lấy thành công",
            length: tickets.length ?? 0,
            data: tickets
        });
    } catch (e) {
        console.log("AccountController - GetMyTicket:", e);
        res.status(500).json({ message: "Server error" });
    }
};

module.exports.GetOrderPending = async (req, res) => {
    try {
        const { User } = req.vars;

        // Tìm các BuyTicket phù hợp
        const buyTickets = await mongoose.model('buy_tickets').find({
            $or: [
                { members: User._id },
                { accCreate: User._id }
            ]
        })
            .populate({
                path: 'members', // Liên kết members
                select: '-password' // Không trả về trường password nếu có
            })
            .populate({
                path: 'accCreate', // Liên kết accCreate
                select: '-password'
            })
            .populate('event') // Liên kết event
            .populate('discount') // Liên kết discount
            .populate('coupon') // Liên kết coupon
            .populate('payment') // Liên kết payment
            .populate('accPay') // Liên kết accPay
            .populate({
                path: 'ticketInfo', // Liên kết ticketInfo
                match: {
                    $or: [
                        { accPay: { $exists: false } }, // ticketInfo không có accPay
                        { 'ticket.accBuy': { $exists: false } } // ticket trong ticketInfo không có accBuy
                    ]
                },
                populate: {
                    path: 'ticket', // Liên kết sâu tới ticket trong ticketInfo
                    populate: {
                        path: 'info', // Liên kết tiếp tới info trong ticket
                        model: 'ticket_types'
                    }
                }
            })
            .lean(); // Trả về plain object để dễ thao tác hơn

        return res.status(200).json({
            message: "Lấy thành công",
            length: buyTickets.length,
            data: buyTickets
        });
    } catch (e) {
        console.error("AccountController - GetOrderPending:", e);
        return res.status(500).json({ message: "Server error" });
    }
};



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
    try{
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
    catch (e) {
        console.log("Acc Controller - Update: ", e)
        return res.status(500).json({
            message: "Lỗi, thử lại sau"
        })
    }

}
module.exports.AddHistory = async(req, res)=>{
    try{
        var {search} = req.body
        var {User} = req.vars
        User.hisSearch.push(search)
        User = await User.save();
        res.status(200).json({
            message: "Thêm lịch sử thành công",
            data: User
        });
    }
    catch (e) {
        console.log("Acc Controller - Update: ", e)
        return res.status(500).json({
            message: "Lỗi, thử lại sau"
        })
    }
}
module.exports.DeleteHistory = async(req, res)=>{
    try{

        var {User} = req.vars
        User.hisSearch = []
        User = await User.save();
        res.status(200).json({
            message: "Xóa lịch sử thành công",
            data: User
        });
    }
    catch (e) {
        console.log("Acc Controller - Update: ", e)
        return res.status(500).json({
            message: "Lỗi, thử lại sau"
        })
    }
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
