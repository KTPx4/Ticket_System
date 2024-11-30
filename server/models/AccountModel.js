const mongoose = require('mongoose')
const {Schema} = mongoose
var root = process.env.IMG || 'localhost:3001/images'

var AccountSchema = new mongoose.Schema({
    name: String,
    address: String,

    pass: { type: String, required: true },
    phone: { type: String, unique: true, required: true },
    email: { type: String, required: true},
    point: { type: Number, default: 0 },
    isDeleted: {type: Boolean, default: false},
    image: {
        type: String,
        get: v=> `${root}/${v}`
    },
    follows:{
        event: [
            {
                type: Schema.Types.ObjectId,
                ref: "events"
            }
        ],
        artist: [
            {
                type: Schema.Types.ObjectId,
                ref: 'artists'
            }
        ]
    },
    friends:[
        {
            type: Schema.Types.ObjectId,
            ref: 'accounts'
        }
    ],
    settings:{
        isNotifyNewEvent: { type: Boolean, default: true }
    },
    notify: [
        {
            news: {
                type: Schema.Types.ObjectId,
                ref: "news",
                default: null
            },
            title: {type: String, default: null},
            content: {type: String, default: null},
            isRead: {type: Boolean, default: false}
        }
    ],
    hisSearch:[
        {type: String}
    ],
    hisEvent:[
        {
            event: {
                type: Schema.Types.ObjectId,
                ref: "events",
            },
            file: [
                {
                    typeFile: {type: String, default: "image"},
                    url: {type: String, default: null}
                }
            ],
            rating: {
                point: {type: Number, default: -1},
                comment: {type: String}
            }
        }
    ]

})
module.exports = mongoose.model('accounts', AccountSchema)