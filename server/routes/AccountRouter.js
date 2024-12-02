const express = require('express')
const _APP = express.Router()

const AccountModel = require('../models/AccountModel')


const multer = require("multer");
const ImageValidator = require('../middlewares/ImageValidator');
const AccountValidator = require('../middlewares/account/Account')
const AccountController = require('../controllers/AccountController')


_APP.get('/', AccountValidator.AuthAccount, AccountController.GetMyAccount)
_APP.post('/', AccountValidator.Register, AccountController.Register)
_APP.post('/login',  AccountValidator.Login, AccountController.Login)

module.exports = (root) =>{

    const uploader = multer({dest: root +'/uploads/'})

    _APP.put('/image',
        uploader.single('image'),
        ImageValidator.Single,
        AccountValidator.AuthAccount,
        AccountController.UpdateImage
    )

    return _APP
}