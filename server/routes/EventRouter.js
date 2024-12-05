const express = require('express')
const _APP = express.Router()
const multer = require('multer')

const StaffAuth = require("../middlewares/staffs/Staff");
const ImageValidator = require("../middlewares/ImageValidator");
const EventValidator = require('../middlewares/event/EventValidator')
const AccountAuth = require('../middlewares/account/Account')
const AccountController = require('../controllers/AccountController')
const EventController = require('../controllers/EventController')

_APP.get('/', EventController.GetAll)
_APP.post('/', StaffAuth.AuthStaff , EventValidator.Create, EventController.Create)

// patch info other field
_APP.get('/:id' , EventValidator.IsExistEvent, EventController.GetByID)
_APP.delete('/:id' , StaffAuth.AuthStaff, EventValidator.IsExistEvent, EventController.DeleteByID)
_APP.patch('/:id' ,  StaffAuth.AuthStaff , EventValidator.IsExistEvent, EventValidator.Update, EventController.Update)

_APP.post('/:id/follow', EventValidator.IsExistEvent, AccountAuth.AuthAccount, AccountController.FollowEvent ) // account follow + use auth account -> get id of account
_APP.delete('/:id/follow' , EventValidator.IsExistEvent, AccountAuth.AuthAccount, AccountController.UnFollowEvent ) // account unfollow + use auth account -> get id of account

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
_APP.get('/:id/news', EventValidator.IsExistEvent, EventController.getNews ) // get all news of this event

// ticket
_APP.get('/:id/ticket', EventValidator.IsExistEvent, EventController.getTicket ) // get all news of this event
_APP.put('/:id/ticket', StaffAuth.AuthStaff, EventValidator.IsExistEvent, EventValidator.UpdateTicketPrice, EventController.UpdateTicketPrice) // get all news of this event

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

    // image of event
    _APP.put('/:id/image',
            uploader.single('image'),
            StaffAuth.AuthStaff ,
            EventValidator.IsExistEvent,
            ImageValidator.Single,
            EventController.PutImage
        ) // upload trailer

    // _APP.put('/:id/image',
    //     StaffAuth.AuthRole(['admin', 'manager']),
    //     uploader.single('image'),
    //     ImageValidator.Single ,
    //     ArtistValidator.isExistID,
    //     ArtisController.UpdateImage)

    return _APP
}