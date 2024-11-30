const express = require('express')
const _APP = express.Router()
const multer = require('multer')

const StaffAuth = require('../middlewares/staffs/Staff')
const ArtistValidator = require('../middlewares/artist/Artist')
const ArtisController = require('../controllers/Staff/ArtistController')

_APP.post('/', StaffAuth.AuthRole(['admin', 'manager']), ArtistValidator.create, ArtisController.create)

module.exports = (root) =>{

    const uploader = multer({dest: root +'/uploads/'})

    // _APP.put('/image', uploader.single('image'), StaffAuth.AuthStaff, Validator.UpdateImage,  Controller.UpdateImage)

    return _APP
}