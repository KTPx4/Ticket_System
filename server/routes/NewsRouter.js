const express = require('express')
const _APP  = express.Router()
const NewsController = require('../controllers/NewsController')
const NewsValidator = require('../middlewares/event/NewsValidator')


_APP.get("/", NewsController.GetNewsByEvent)
_APP.post('/', NewsValidator.Create, NewsController.Create)


module.exports = _APP