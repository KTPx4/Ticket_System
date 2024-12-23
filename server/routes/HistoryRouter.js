const express = require('express')
const path = require('path');
const multer = require("multer");

const _APP = express.Router()

const AuthAccount = require('../middlewares/account/Account')
const HistoryMid = require('../middlewares/history/History')
const UploadValidator = require('../middlewares/UploadValidator')
const EventValidator = require('../middlewares/event/EventValidator')

const Controller = require('../controllers/HistoryController')

// get all history of event
_APP.get('/:id/me' ,AuthAccount.AuthAccount,EventValidator.IsExistEvent, Controller.GetMyPost)

_APP.get('/:id', AuthAccount.AuthAccount, EventValidator.IsExistEvent, Controller.GetAll)

_APP.post('/',
    UploadValidator.UploadArr,
    AuthAccount.AuthAccount,
    HistoryMid.CanPost,
    UploadValidator.ArrayFile,
    Controller.PostHistory
)
_APP.delete('/:id', AuthAccount.AuthAccount, HistoryMid.CanDelete, Controller.DeleteById)


module.exports = _APP