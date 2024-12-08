const express = require('express')
const _APP = express.Router()
const multer = require('multer')

const StaffAuth = require('../middlewares/staffs/Staff')
const AuthAccount = require('../middlewares/account/Account')
const ArtistValidator = require('../middlewares/artist/Artist')
const ArtisController = require('../controllers/ArtistController')
const ImageValidator = require('../middlewares/ImageValidator')

_APP.get('/', ArtisController.GetAll)

_APP.post('/', StaffAuth.AuthRole(['admin', 'manager']), ArtistValidator.create, ArtisController.Create)

_APP.patch('/:id' ,  StaffAuth.AuthRole(['admin', 'manager']), ArtistValidator.isExistID,  ArtisController.Update )

_APP.post('/:id/follow', AuthAccount.AuthAccount, ArtistValidator.isExistID,  ArtisController.Follow)
_APP.delete('/:id/follow', AuthAccount.AuthAccount, ArtistValidator.isExistID, ArtisController.UnFollow )

module.exports = (root) =>{

    const uploader = multer({dest: root +'/uploads/'})

     _APP.put('/:id/image',
         StaffAuth.AuthRole(['admin', 'manager']),
         uploader.single('image'),
         ImageValidator.Single ,
         ArtistValidator.isExistID,
         ArtisController.UpdateImage)

    return _APP
}