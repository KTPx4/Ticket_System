const express = require('express')
const _APP = express.Router()
const multer = require('multer')

const StaffAuth = require("../middlewares/staffs/Staff");
const ImageValidator = require("../middlewares/ImageValidator");
const EventValidator = require('../middlewares/event/EventValidator')
const EventController = require('../controllers/EventController')

_APP.get('/', EventController.GetAll)
_APP.post('/', StaffAuth.AuthStaff , EventValidator.Create, EventController.Create)

// patch info other field
_APP.patch('/:id' ,  StaffAuth.AuthStaff , EventValidator.IsExistEvent, EventValidator.Update, EventController.Update)

_APP.post('/:id/follow'  ) // account follow + use auth account -> get id of account
_APP.delete('/:id/follow' ) // account unfollow + use auth account -> get id of account
//-----------------
// update location ,date
_APP.put('/:id/location', StaffAuth.AuthStaff ,  EventValidator.IsExistEvent)
_APP.put('/:id/date',  StaffAuth.AuthStaff , EventValidator.IsExistEvent)

// image, video demo event
_APP.post('/:id/file',  StaffAuth.AuthStaff , EventValidator.IsExistEvent) // upload list image review event
_APP.delete('/:id/file',  StaffAuth.AuthStaff , EventValidator.IsExistEvent) // delete buy position in query string ?p=

// Image, video after event
_APP.post('/:id/after',  StaffAuth.AuthStaff , EventValidator.IsExistEvent) // upload list, image video after event
_APP.delete('/:id/after',  StaffAuth.AuthStaff , EventValidator.IsExistEvent) // delete buy position in query string ?p=

// Get news
_APP.get('/:id/news', EventValidator.IsExistEvent ) // get all news of this event

module.exports = (root) =>{

    const uploader = multer({dest: root +'/uploads/'})

    //trailer of event
    _APP.put('/:id/trailer',
            uploader.single('trailer'),
            StaffAuth.AuthStaff ,
            EventValidator.IsExistEvent,
            ImageValidator.Trailer,
            EventController.PutTrailer
        ) // upload trailer

    // _APP.put('/:id/image',
    //     StaffAuth.AuthRole(['admin', 'manager']),
    //     uploader.single('image'),
    //     ImageValidator.Single ,
    //     ArtistValidator.isExistID,
    //     ArtisController.UpdateImage)

    return _APP
}