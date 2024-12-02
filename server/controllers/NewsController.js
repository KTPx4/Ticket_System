const NewsModel = require('../models/NewsModel')
const EventModel = require('../models/EventModel')
const mongoose = require("mongoose");


module.exports.GetNewsByEvent = async(req, res)=>{
   try{
       let {event}= req.query
       var data = null
       if(event && mongoose.Types.ObjectId.isValid(event))
       {
           data = await NewsModel.find({event: event})
               .populate('event')
       }
       else{
           data = await NewsModel.find()
               .populate('event')
       }

       return res.status(200).json({
           message: "Danh sách tin tức",
           data: data
       })
   }
   catch (e)
   {
       return res.status(500).json({
           message: "Lỗi. Thử lại sau"
       })
   }

}

module.exports.Create = async(req, res)=>{
    try{
        let {Event}= req.vars
        let {title, content} = req.body
        var news = await NewsModel.create({
            event: Event._id,
            title: title,
            content: content
        })

        Event.news.push(news)
        await Event.save()
        return res.status(201).json({
            message: "Tạo tin thành công",
            data: news
        })
    }
    catch (e)
    {
        console.log("Error at News controller - create: ", e)
        return res.status(500).json({
            message: "Tạo tin thất baại"
        })
    }
}