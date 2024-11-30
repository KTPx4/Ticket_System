const express = require('express')
const _APP = express.Router()
const multer = require('multer')

const StaffAuth = require('../middlewares/staffs/Staff')
const Validator = require('../middlewares/staffs/Validator')
const Controller = require('../controllers/Staff/StaffController')

// register
_APP.post('/register', Validator.register, Controller.register)
_APP.post('/login', Validator.login, Controller.login)

// _APP.post('/ticket', StaffAuth.AuthStaff, Controller.ticket)


module.exports = (root) =>{

    const uploader = multer({dest: root +'/uploads/'})

    _APP.put('/image', uploader.single('image'), StaffAuth.AuthStaff, Validator.UpdateImage,  Controller.UpdateImage)

    return _APP
}