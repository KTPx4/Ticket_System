const express = require('express')
const _APP  = express.Router()
const NewsController = require('../controllers/NewsController')
const NewsValidator = require('../middlewares/event/NewsValidator')


_APP.get("/", NewsController.GetNewsByEvent)

_APP.get("/:id", NewsController.GetById)

_APP.post('/', NewsValidator.Create, NewsController.Create)

_APP.post('/test', NewsValidator.Create, NewsController.CreateExample)


module.exports = _APP