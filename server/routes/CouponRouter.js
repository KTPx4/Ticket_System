const express = require('express')
const _APP = express.Router()
const Validator = require('../middlewares/coupon/Validator')
const Controller = require('../controllers/CouponController')

_APP.post('/', Validator.Create, Controller.Create)



module.exports = _APP