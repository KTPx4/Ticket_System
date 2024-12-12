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


// change list member / typePayment for order by (event & user)
_APP.put('/:id' , AuthAcc.AuthAccount, Validator.isExistsBuyTicket , Validator.Update, Controller.Update)

//delete ticket
_APP.delete('/:id' , AuthAcc.AuthAccount, Validator.isExistsBuyTicket , Validator.Delete, Controller.Delete)

//----------- id of buy ticket model
// add coupon -> create payment detail with code and wait user pay
// -> post /valid check code of payment with api
// -> get money, code from api payment gateway to check with payment detail

// id of buy ticket model
_APP.post('/:id/checkout', AuthAcc.AuthAccount,  Validator.GetCheckOut, Controller.GetCheckOut)

_APP.post('/:id/valid', AuthAcc.AuthAccount,  Validator.ValidOrder, Controller.ValidOrder)
_APP.post('/:id/stripe-checkout', AuthAcc.AuthAccount, Validator.GetCheckOut, Controller.StripeCheckout);


module.exports = _APP