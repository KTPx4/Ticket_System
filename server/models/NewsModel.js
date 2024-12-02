const mongoose = require('mongoose')
const {Schema} = mongoose
const moment = require('moment'); // Thư viện định dạng ngày tháng

const NewsSchema = new mongoose.Schema({
    event: {
        type: Schema.Types.ObjectId,
        ref: 'events'
    },
    title: String,
    content: String,
    desc:{
        type: String
    },
    file:[
        {
            typeFile: {type: String, default: "image"},
            url: {type: String, default: null}
        }
    ],
    createdAt: {
        type: Date,
        default: Date.now(),
        get: (v) => moment(v).format('DD/MM/YYYY HH:mm'), // Getter định dạng
    }
},
    {
        toJSON: { getters: true }, // Bật chế độ getter khi convert sang JSON
        toObject: { getters: true }, // Bật chế độ getter khi convert sang Object
    })
module.exports = mongoose.model('news', NewsSchema)