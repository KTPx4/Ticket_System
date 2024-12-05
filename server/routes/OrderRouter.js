const express = require('express')
const _APP = express.Router()
const Validator = require('../middlewares/order/Order')
const Controller = require('../controllers/OrderController')
const AuthAcc = require('../middlewares/account/Account')
const EventValidator = require('../middlewares/event/EventValidator')
//----------- id of event
// get all order of user
_APP.get('/', AuthAcc.AuthAccount, Controller.GetAll)

// create order for user
_APP.post('/', AuthAcc.AuthAccount, Validator.Create, Controller.Create)

// get order by (event & user)
_APP.get('/:id' , AuthAcc.AuthAccount, EventValidator.IsExistEvent, Controller.GetByEvent)

// change list member / typePayment for order by (event & user)
_APP.put('/:id' , AuthAcc.AuthAccount, EventValidator.IsExistEvent, Validator.Update, Controller.Update)

//----------- id of buy ticket model
// add coupon -> create payment detail with code and wait user pay
// -> post /valid check code of payment with api
// -> get money, code from api payment gateway to check with payment detail
_APP.post('/:id/checkout', AuthAcc.AuthAccount, )
_APP.post('/:id/valid', AuthAcc.AuthAccount, )


module.exports = _APP