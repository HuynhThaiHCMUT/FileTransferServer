<a id="readme-top"></a>





<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/HuynhThaiHCMUT/FileTransfer">
    <img src="hcmut.png" alt="Logo" width="160" height="160">
  </a>

<h3 align="center">File Sharing Application (Server)</h3>

  <p align="center">
    Một ứng dụng chia sẻ file được hiện thực sử dụng giao thức tầng ứng dụng tự định nghĩa dựa trên TCP/IP.
    <br />
    <a href="#getting-started"><strong>Xem hướng dẫn »</strong></a>
    <br />
    <br />
    <a href="https://github.com/HuynhThaiHCMUT/FileTransferServer/releases">Tải xuống</a>
    ·
    <a href="mailto:phuong.ngo0320@hcmut.edu.vn">Báo lỗi</a>
    ·
    <a href="mailto:phuong.ngo0320@hcmut.edu.vn">Đề xuất tính năng</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Mục lục</summary>
  <ol>
    <li>
      <a href="#about-the-project">Về dự án này</a>
      <ul>
        <li><a href="#built-with">Công nghệ sử dụng</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Bắt đầu</a>
      <ul>
        <li><a href="#prerequisites">Điều kiện</a></li>
        <li><a href="#installation">Cài đặt</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Hướng dẫn sử dụng</a>
      <ul>
        <li><a href="#notes">Một số lưu ý</a></li>
        <li><a href="#functions">Thao tác trong ứng dụng</a></li>
        <li><a href="#errors">Lỗi có thể xảy ra khi sử dụng</a></li>
      </ul>
    </li>
    <li><a href="#contact">Liên hệ</a></li>
    <li><a href="#acknowledgments">Tài liệu tham khảo</a></li>
  </ol>
</details>





<!-- ABOUT THE PROJECT -->
<a id="about-the-project"></a>

## Về dự án này

Ứng dụng này được hiện thực để phục vụ cho môn học Mạng máy tính, thuộc về Trường Đại học Bách khoa, ĐHQG TP.HCM. Dự án hướng đến mục tiêu xây dựng nên một hệ thống chia sẻ file dựa theo kiến trúc kết hợp giữa Client-Server và Peer-to-Peer, nhằm tối ưu hóa hiệu suất của hệ thống. Ứng dụng này dành cho phía Server, người có thể kiểm tra trạng thái online/offline và xem thông tin file của các Client.

Link dự án: 

- Client Application: [https://github.com/HuynhThaiHCMUT/FileTransfer](https://github.com/HuynhThaiHCMUT/FileTransfer)
- Server Application: [https://github.com/HuynhThaiHCMUT/FileTransferServer](https://github.com/HuynhThaiHCMUT/FileTransferServer)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Công nghệ sử dụng
<a id="built-with"></a>

- [Java](https://www.java.com)
- [JavaFX](https://openjfx.io)
- [Apache Maven](https://maven.apache.org)

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- GETTING STARTED -->
<a id="getting-started"></a>

## Bắt đầu

<a id="prerequisites"></a>
### Điều kiện

Hiện tại, ứng dụng chỉ có bản tập tin thực thi trên nền tảng Windows

Source code của ứng dụng được phát triển sử dụng JDK 17 của Bellsoft Liberica và được xây dựng bằng Apache Maven

<a id="installation"></a>
### Cài đặt


1. Truy cập đường dẫn sau: [Download FileTransferServer](https://github.com/HuynhThaiHCMUT/FileTransferServer/releases)
2. Tải về file `FileTransferServer.exe` 
3. Nhấn đúp vào `FileTransferServer.exe` để bắt đầu sử dụng

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- USAGE -->
<a id="usage"></a>

## Hướng dẫn sử dụng

<a id="notes"></a>

### Một số lưu ý

- Để sử dụng ứng dụng này, bạn cần có một kết nối internet ổn định.

<a id="functions"></a>

### Thao tác trong ứng dụng

Ứng dụng phía Server này cung cấp giao diện CLI với các lệnh sau:

| Cú pháp | Chức năng                             | Mô tả                                                                                               | Ví dụ |
|-|---------------------------------------|-----------------------------------------------------------------------------------------------------|-|
| `help`  | Trợ giúp                              | Hiển thị danh sách các lệnh có sẵn cùng với cú pháp và cách sử dụng, dùng trong trường hợp bạn quên | | |
| `start` | Khởi động Network Server              | Lệnh để khởi động server, bắt đầu nhận các yêu cầu từ client                                        | |
| `stop`  | Tạm dừng Network Server               | Lệnh để tạm dừng server, không xử lí các yêu cầu từ client                                          | |
| `clear` | Xóa toàn bộ nội dung terminal         | Màn hình hiển thị của Terminal sẽ được reset lại như ban đầu                                        | |
| `ping <hostname>` | Kiểm tra trạng thái của Client        | Thay `<hostname>` bằng username của Client cần kiểm tra                                             | `ping phuongngo` |
| `discover <hostname>` | Xem danh sách file đã đăng của Client | Thay `<hostname>` bằng username của Client cần kiểm tra                                             | `discover Thai` |

<a id="errors"></a>

### Lỗi có thể xảy ra khi sử dụng

- Connection error: Các lỗi có thể xảy ra khi tạo đường truyền kết nối đến client
- Database error: Các lỗi có thể xảy ra do cơ sở dữ liệu

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- CONTACT -->
<a id="contact"></a>

## Liên hệ

Thành viên của dự án:

- Đinh Huỳnh Thái - thai.dinhhuynh@hcmut.edu.vn
- Lê Thanh Tùng - thanhtunga52021@gmail.com
- Nguyễn Thị Xuân Hoa - nguyenhoacujut2003@gmail.com
- Ngô Văn Phương - phuong.ngo0320@hcmut.edu.vn

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- ACKNOWLEDGMENTS -->
<a id="acknowledgments"></a>

## Tài liệu tham khảo

* Kurose, J. and Ross, K. (2022). _Computer Networking: a top-down approach, 8th edition_. Boston: Pearson Education Limited.
* [JavaFX Documentation](https://openjfx.io)

<p align="right">(<a href="#readme-top">back to top</a>)</p>