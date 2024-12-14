const express = require('express')
const _APP = express.Router()
const Validator = require('../middlewares/coupon/Validator')
const Controller = require('../controllers/CouponController')
const AuthAccount = require('../middlewares/account/Account')

_APP.post('/', Validator.Create, Controller.Create)

_APP.get('/', AuthAccount.AuthAccount, Controller.GetAllPublic)
_APP.get('/me', AuthAccount.AuthAccount, Controller.GetMyCoupon)
_APP.post('/exchange', AuthAccount.AuthAccount, Validator.ChangeCoupon ,  Controller.ChangeCoupon)
module.exports = _APP