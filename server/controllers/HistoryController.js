const path = require('path');
const AccountModel = require('../models/AccountModel')
const EventModel = require('../models/EventModel')
const HistoryModel = require('../models/HistoryEventModel')
const UploadFile = require('../controllers/Upload')

const LIMIT_PAGE = 5; // Số lượng item mỗi trang

const mongoose = require('mongoose')
const {populate} = require("dotenv");

module.exports.GetMyPost = async (req, res)=>{
    try{
        var {User} = req.vars
        var userId = User._id
        const { id } = req.params;
        var his = await HistoryModel.findOne({
            account: userId,
            event: id
        })
            .populate({
                path: "account",
                select: "_id name email image"
            })

        return res.status(200).json({
            message: "Lấy dữ liệu thành công",
            data: his
        })
    }
    catch (err) {
        console.error(err);
        res.status(500).json({ message: 'Vui lòng thử lại sau' });
    }
}

module.exports.GetAll = async (req, res) => {
    try {
        const { id } = req.params;
        var {Event, User} = req.vars
        var userId= User._id
        var canPost = Event.accJoins?.includes(userId)

        // Lấy page từ query string, mặc định là 0 nếu không có
        const page = parseInt(req.query.page) || 0;
        const limit = LIMIT_PAGE; // Số lượng item mỗi trang

        // Tính toán số bản ghi cần bỏ qua dựa trên page và limit
        const skip = page * limit;

        // Lấy tổng số bản ghi
        const totalCount = await HistoryModel.countDocuments({ event: id });

        // Tính toán trung bình rating sử dụng Aggregate
        const avgRatingResult = await HistoryModel.aggregate([
            { $match: { event:new mongoose.Types.ObjectId(id) } }, // Lọc theo event
            { $match: { rating: { $gte: 0, $lte: 5 } } },  // Lọc rating hợp lệ
            { $group: { _id: null, avgRating: { $avg: "$rating" } } } // Tính trung bình rating
        ]);

        const avgRating = avgRatingResult.length > 0 ? avgRatingResult[0].avgRating : null;

        // Lấy danh sách history của sự kiện, sắp xếp theo createdAt (mới nhất) và phân trang
        const listHist = await HistoryModel.find({ event: id })
            .sort({ createdAt: -1 }) // Sắp xếp theo createdAt mới nhất
            .skip(skip)              // Bỏ qua các bản ghi đã được tải về trong các trang trước
            .limit(limit)           // Giới hạn số lượng bản ghi mỗi trang
            .populate({
                path: "account",
                select: "_id name email image"
            })

        // Tính toán tổng số trang
        const totalPages = Math.ceil(totalCount / limit);

        var dataEvent = {
            _id: Event._id,
            name: Event.name,
            location: Event.location,
            date: Event.date.start,
            rating: avgRating,
            hasPost: canPost
        };

        // Trả về kết quả
        return res.status(200).json({
            message: "Lấy dữ liệu thành công",
            data: listHist,
            event: dataEvent,
            pagination: {
                currentPage: page,
                totalPages: totalPages,
                totalCount: totalCount,
                hasNextPage: page < totalPages - 1,  // Kiểm tra xem có trang kế tiếp hay không
                hasPrevPage: page > 0               // Kiểm tra xem có trang trước hay không
            }
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ message: 'Vui lòng thử lại sau' });
    }
};


module.exports.PostHistory = async (req, res)=>{
    try {

        const { event, rating, comment } = req.body;
        // const { accountId, eventId, rating, comment } = req.body;
        const files = req.files;
        var accountId = req.vars.User._id
        var eventId = event

        // Tạo History Event
        const newHistory = new HistoryModel({
            account: accountId,
            event: eventId,
            rating,
            comment,
        });

        // Lưu history để lấy _id
        const savedHistory = await newHistory.save()


        // Di chuyển file vào thư mục tương ứng
        const rootPath = req.vars.root // Thư mục gốc của project
        const currPath =  path.join(rootPath, 'public', 'history', savedHistory._id.toString());

        const fileData = await UploadFile.UploadArray(files, currPath );

        // Cập nhật thông tin file trong History Event
        savedHistory.file = fileData;

        await savedHistory.save()

        // Populate trường account trước khi trả về
        const populatedHistory = await HistoryModel.findById(savedHistory._id)
            .populate('account', 'name email image'); // Chỉ chọn các trường cần thiết


        return res.status(201).json({
            message: 'Tạo thành công.',
            data: populatedHistory,
        });

    }
    catch (err) {
        console.error(err);
        res.status(500).json({ message: 'Vui lòng thử lại sau'});
    }
}

module.exports.DeleteById = async(req, res)=>{
    try {
        var {id} = req.params

        const rootPath = req.vars.root // Thư mục gốc của project
        const currPath =  path.join(rootPath, 'public', 'history', id);
        await UploadFile.DeleteFolder(currPath);
        var his = await HistoryModel.findByIdAndDelete(id)
            .populate('account', 'name email image')

        return res.status(200).json({
            message: 'Xóa thành công.' ,
            data: his
        });

    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Vui lòng thử lại sau.' });
    }


}

module.exports.CreateExample = async (req, res)=>{
    try{
        var listEvent = await EventModel.find()
        for(const event of listEvent)
        {
            var eventId = event._id
            var comments = await HistoryModel.find({event: eventId})
            if(!comments || comments.length < 2)
            {
                const listComment = [
                    {account: "674dbb2725fe9b5bc9639895", event: eventId, rating: 3, comment: `Ok lắm nha - ${Date.now()}`},
                    {account: "674dbe8625fe9b5bc963989c", event: eventId, rating: 4, comment: `Toẹt cà là vời - ${Date.now()}`},
                    {account: "675e3003b84676d9291dc798", event: eventId, rating: 5, comment: `Đỉnh chóp - ${Date.now()}`},
                    {account: "676964a7bad5500999ccd108", event: eventId, rating: 3, comment: `Qúa tuyệt vời - ${Date.now()}`},
                    {account: "676964afbad5500999ccd10b", event: eventId, rating: 5, comment: `Thật bất ngờ hihi - ${Date.now()}`},
                    {account: "6769649dbad5500999ccd105", event: eventId, rating: 5, comment: `Trời ơi đỉnh - ${Date.now()}`},
                    {account: "67696495bad5500999ccd102", event: eventId, rating: 5, comment: `Nổ rồi các cháu ơi- ${Date.now()}`},
                ]
                await HistoryModel.insertMany(listComment)
            }
        }

        return  res.status(200).json({
            message: "Đã tạo xong comment cho tất cả event"
        })
    }
    catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Vui lòng thử lại sau.' });
    }
}