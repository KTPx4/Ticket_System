const fs = require('fs').promises;
const path = require('path');

module.exports.uploadSingle = async (file, currentPath, id) => {
    try {
        if (file) {
            // Kiểm tra và tạo thư mục nếu không tồn tại
            await fs.mkdir(currentPath, { recursive: true });

            const name = file.originalname;
            const temp = name.split('.');
            const extension = temp[temp.length - 1];
            const newFilePath = `${currentPath}/${id}.${extension}`;

            // Di chuyển file đến thư mục mới
            await fs.rename(file.path, newFilePath);

            return `${id}.${extension}`;

        }
        return undefined;
    } catch (error) {
        console.error("Error in uploadSingle: ", error.message);
        throw error; // Throw error để xử lý lỗi ở nơi gọi hàm
    }
};

module.exports.UploadArray = async (files, currPath) => {
    try {
        const historyFolder = currPath;
        // const historyFolder = path.join(rootPath, 'public', 'history', modelId.toString());

        // Tạo thư mục nếu chưa tồn tại
        try {
            await fs.mkdir(historyFolder, { recursive: true });
        } catch (err) {
            console.error('Error creating folder:', err);
            throw new Error('Không thể tạo thư mục lưu trữ.');
        }

        const fileData = [];

        // Di chuyển từng file từ `uploads` sang `public/history/<modelId>`
        for (const file of files) {
            const destinationPath = path.join(historyFolder, file.filename);
            try {
                await fs.rename(file.path, destinationPath);
            } catch (err) {
                console.error(`Error moving file ${file.filename}:`, err);
                throw new Error('Không thể di chuyển file.');
            }

            // Đẩy thông tin file vào danh sách
            fileData.push({
                typeFile: file.mimetype.startsWith('image') ? 'image' : 'video',
                name: file.filename, // Lưu tên file để quản lý
            });
        }
        return fileData;

    } catch (error) {
        console.error('Error in moveFilesToHistoryFolder:', error);
        throw error;
    }
};

module.exports.DeleteFolder = async (folderPath)=>{
    try {
        // Kiểm tra xem thư mục có tồn tại không
        await fs.access(folderPath);

        // Đọc tất cả nội dung trong thư mục
        const files = await fs.readdir(folderPath);

        // Xóa từng file/directory bên trong thư mục
        await Promise.all(
            files.map(async (file) => {
                const filePath = path.join(folderPath, file);
                const stat = await fs.stat(filePath);

                if (stat.isDirectory()) {
                    // Đệ quy xóa nếu là thư mục
                    await deleteFolder(filePath);
                } else {
                    // Xóa file
                    await fs.unlink(filePath);
                }
            })
        );

        // Xóa thư mục rỗng sau khi đã xóa nội dung
        await fs.rmdir(folderPath);

        console.log(`Đã xóa thư mục: ${folderPath}`);

    } catch (error) {
        console.error(`Không thể xóa thư mục ${folderPath}:`, error.message);
        throw error; // Bắn lỗi để controller xử lý
    }
}