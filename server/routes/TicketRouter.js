const express = require('express')
const _APP = express.Router()
const StaffAuth = require('../middlewares/staffs/Staff')
const Validator = require('../middlewares/ticket/Validator')
const Controller = require('../controllers/TicketController')

_APP.put('/price', StaffAuth.AuthStaff, Validator.UpdatePrice, Controller.UpdatePrice)
_APP.put('/all', StaffAuth.AuthStaff, Validator.UpdateAll, Controller.UpdateAll )
module.exports = _APP