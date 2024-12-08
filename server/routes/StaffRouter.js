const express = require('express')
const _APP = express.Router()
const multer = require('multer')

const StaffAuth = require('../middlewares/staffs/Staff')
const Validator = require('../middlewares/staffs/Validator')
const Controller = require('../controllers/StaffController')
const ImageValidator = require("../middlewares/ImageValidator");
const AccountValidator = require("../middlewares/account/Account");


// register
_APP.post('/register', StaffAuth.AuthRole(["admin", "manager"]), Validator.register, Controller.register)
_APP.post('/login', Validator.login, Controller.login)

_APP.get('/verify', StaffAuth.AuthStaff, (req, res)=>{
    return res.status(200).json({
        message: "Token hợp lệ",
        data: req.vars.Staff._id ?? ""
    })
})
// _APP.post('/ticket', StaffAuth.AuthStaff, Controller.ticket)


module.exports = (root) =>{

    const uploader = multer({dest: root +'/uploads/'})

    _APP.put('/image',
        uploader.single('image'),
        StaffAuth.AuthStaff,
        ImageValidator.Single,
        Controller.UpdateImage)

    return _APP
}