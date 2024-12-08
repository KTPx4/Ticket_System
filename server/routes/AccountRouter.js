const express = require('express')
const _APP = express.Router()

const AccountModel = require('../models/AccountModel')


const multer = require("multer");
const ImageValidator = require('../middlewares/ImageValidator');
const AccountValidator = require('../middlewares/account/Account')
const AccountController = require('../controllers/AccountController')

_APP.get('/', AccountValidator.AuthAccount, AccountController.GetMyAccount)
_APP.post('/', AccountValidator.Register, AccountController.Register)
_APP.patch('/', AccountValidator.AuthAccount, AccountValidator.Update, AccountController.Update)
_APP.put('/password', AccountValidator.AuthAccount, AccountValidator.Password, AccountController.ChangePass)
_APP.post('/login',  AccountValidator.Login, AccountController.Login)

_APP.post('/history' , AccountValidator.AuthAccount, AccountValidator.History, AccountController.AddHistory )
_APP.delete('/history' , AccountValidator.AuthAccount, AccountController.DeleteHistory )

_APP.get('/ticket', AccountValidator.AuthAccount, AccountController.GetMyTicket)
_APP.get('/pending', AccountValidator.AuthAccount, AccountController.GetOrderPending)

_APP.get('/reset', AccountValidator.GetReset ,AccountController.GetReset)
_APP.post('/reset', AccountValidator.SendReset ,AccountController.SendReset)

_APP.get('/verify', AccountValidator.AuthAccount, (req, res)=>{
    return res.status(200).json({
        message: "Token hợp lệ",
        data: req.vars.User._id ?? ""
    })
})

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