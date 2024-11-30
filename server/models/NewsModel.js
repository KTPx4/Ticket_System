const mongoose = require('mongoose')
const {Schema} = mongoose

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
    ]
})
module.exports = mongoose.model('news', NewsSchema)