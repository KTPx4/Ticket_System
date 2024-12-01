const mongoose = require('mongoose')
const {Schema} = mongoose
// Thư viện để định dạng ngày tháng
const moment = require('moment');

const EventSchema = new mongoose.Schema({
    name: String,
    location: String,
    desc: {
        type: String
    },  
    type: [{type: String}],
    tag: [{type: String}],
    priceRange: {
        min: {type: Number, default: 0},
        max: {type: Number, default: 0}
    },
    date:{
        start: {
            type: Date,
            get: (v) => moment(v).format('DD/MM/YYYY HH:mm') // Getter định dạng
        },
        end: {
            type: Date,
            get: (v) => moment(v).format('DD/MM/YYYY HH:mm') // Getter định dạng
        }
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
    accJoins: [
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
}, {
    toJSON: { getters: true }, // Bật chế độ dùng getter khi convert sang JSON
    toObject: { getters: true } // Bật chế độ dùng getter khi convert sang Object
})
module.exports = mongoose.model('events', EventSchema)