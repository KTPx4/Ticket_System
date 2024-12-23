const mongoose = require('mongoose')
const {Schema} = mongoose

const HistoryModel = new mongoose.Schema({
    account: {
        type: Schema.Types.ObjectId,
        ref: "accounts"
    },
    event: {
        type: Schema.Types.ObjectId,
        ref: "events",
    },
    file: [
        {
            typeFile: {type: String, default: "image"},
            name: {type: String, default: null}
        }
    ],
    rating: {type: Number, default: 5},
    comment: {type: String},
    createdAt:{
        type: Date,
        default: Date.now()
    }

})

module.exports = mongoose.model("history_events", HistoryModel)