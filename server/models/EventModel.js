const mongoose = require('mongoose')
const {Schema} = mongoose

const EventSchema = new mongoose.Schema({
    name: String,
    location: String,
    desc: {
        type: String
    },  
    type: [{type: String}],
    tag: [{type: String}],
    date:{
        start: {type: Date},
        end: {type: Date}
    },
    isHotEvent: {type: Boolean, default: true},
    follower: [{
        type: Schema.Types.ObjectId,
        ref: 'accounts'
    }],
    file: [
        {
            typeFile: {type: String, default: "image"},
            url: {type: String, default: null}
        }
    ],
    trailer: String,
    artists: [
        {
            type: Schema.Types.ObjectId,
            ref: 'artists'
        }
    ],
    news:[
        {
            type: Schema.Types.ObjectId,
            ref: 'news'
        }
    ],
    accJoin: [
        {
            type: Schema.Types.ObjectId,
            ref: 'accounts'
        }
    ],
    afterEvent: [
        {
            typeFile: {type: String, default: "image"},
            url: {type: String, default: null}
        }
    ]
})
module.exports = mongoose.model('events', EventSchema)