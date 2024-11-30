const StaffModel = require('../../models/StaffModel')

const fs = require('fs');
const bcrypt = require('bcrypt')
const jwt = require('jsonwebtoken');
const SECRET_LOGIN = process.env.KEY_SECRET_LOGIN || 'px4-secret-key-login-app'

module.exports.register = async (req, res)=>{
    let {user, email, name}= req.body
    let {root} = req.vars
    email = email.toLowerCase()
    var account = null
    try{
        bcrypt.hash(email, 10)
            .then(async(hashed) =>{

                const Account = await StaffModel.create({
                    name: name,
                    email: email,
                    user: user,
                    pass: hashed
                }, )
                account = Account

                createFolder(root, Account._id, Account.image) ? null : console.log(`Can't create folder for Account: '${Account._id}' - '${Account.user}'`);

                return res.status(200).json({
                    message: 'Register Success',
                    data: {
                        account: account
                    }
                })
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

module.exports.login = async( req, res) =>{

    let {root, Staff} = req.vars
    var data = {
        user: Staff.user,
        pass: Staff.pass,
        name: Staff.name,
        image: Staff.image
    }
    createFolder(root, Staff._id, Staff.image) ? null : console.log(`Can't create folder for Account: '${Staff._id}' - '${Staff.user}'`);

    await jwt.sign(data, SECRET_LOGIN, {expiresIn: "7d"}, (err, tokenLogin)=>{
        if(err) throw err
        return res.status(200).json({
            status: "Login success",
            message: "Đăng nhập thành công",
            data: tokenLogin
        })
    })

}

module.exports.UpdateImage =async(req, res) =>{
    try
    {
        // console.log("1 request");
        let {root}  = req.vars
        var Staff = req.vars.Staff

        let newNameAVT = await upLoadAvt(req.file, root,  Staff)

        var newAccount = await StaffModel.findOneAndUpdate({_id: Staff._id}, {
            image: `${newNameAVT}`
        }, {new: true})

        return res.status(200).json({
            message: 'Thay đổi thông tin thành công',
            data: newAccount
        })
    }
    catch(err)
    {
        console.log("Error At Staff Contronller - UpdateProfile: ", err.message);
        return res.status(500).json({
            status: "Error Server When Edit",
            message: "Server đang bận. Vui lòng  thử lại sau!"
        })
    }
}

module.exports.ticket = (req, res) =>{
    return res.status(200).json({
        message: "Ok"
    })
}
const upLoadAvt =async (file, root, AccUser)=>{
    //console.log("Upload Avt call");
    let id = AccUser._id

    const currentPath = `${root}/public/account/${id}`

    if(file)
    {
        if(!fs.existsSync(currentPath))
        {
            fs.mkdir(currentPath, (error) =>
            {
                if (error)
                {
                    console.log("Error at create Folder Upload: ", error.message);
                }
            });
        }


        let name = file.originalname
        let temp = name.split('.')
        let extension = temp[temp.length - 1]


        let newFilePath = currentPath + '/' +  `${id}.${extension}`

        fs.renameSync(file.path, newFilePath)

        return `${id}.${extension}`
    }

    return undefined

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
