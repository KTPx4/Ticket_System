const NewsModel = require('../models/NewsModel')
const EventModel = require('../models/EventModel')
const mongoose = require("mongoose");

module.exports.GetById = async (req, res)=>{
    try{
        let {id} = req.params
        var data = await NewsModel.findById(id)
            .populate('event')

        var eventName = ""
        var news = null
        if(data)
        {
            eventName = data.event?.name ?? ""
            news = {
                _id: data._id ?? "",
                title: data.title ?? "",
                content: data.content ?? "",
                desc: data.desc ?? "",
                file: data.file ?? [],
                createdAt: data.createdAt ?? ""
            }

            return res.status(200).json({
                message: "Lấy thông tin thành công",
                data: news,
                eventName: eventName
            })
        }
        else
        {
            return res.status(400).json({
                message: "Tin tức không tồn tại hoặc bị xóa"
            })
        }


    }
    catch (e)
    {
        return res.status(500).json({
            message: "Lỗi. Thử lại sau"
        })
    }
}

module.exports.GetNewsByEvent = async(req, res)=>{
   try{
       let {event}= req.query
       var data = null
       if(event && mongoose.Types.ObjectId.isValid(event))
       {
           data = await NewsModel.find({event: event})
               .sort({ createdAt: -1 }) // Sắp xếp theo `createdAt` giảm dần (mới nhất trước)
               .populate('event')
       }
       else{
           data = await NewsModel.find()
               .sort({ createdAt: -1 }) // Sắp xếp theo `createdAt` giảm dần (mới nhất trước)
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

module.exports.CreateExample = async(req, res)=>{
    try{
        let {Event}= req.vars
        var title = "Tin tức sự kiện - " + Date.now()
        var content = `Đây là bản tin test cho sự kiện ${Event._id}. Được tạo ra nhằm mục đích test`

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