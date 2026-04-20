-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Apr 20, 2026 at 08:07 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `etco_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `chat_message`
--

CREATE TABLE `chat_message` (
                                `id` bigint NOT NULL,
                                `content` mediumtext,
                                `created_at` datetime(6) DEFAULT NULL,
                                `created_by` varchar(255) DEFAULT NULL,
                                `has_image` bit(1) NOT NULL,
                                `iteration_count` int NOT NULL,
                                `role` varchar(255) DEFAULT NULL,
                                `session_id` varchar(255) DEFAULT NULL,
                                `tool_used` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

CREATE TABLE `documents` (
                             `id` bigint NOT NULL,
                             `created_at` datetime(6) DEFAULT NULL,
                             `created_by` varchar(255) DEFAULT NULL,
                             `file_name` varchar(255) DEFAULT NULL,
                             `file_size` bigint NOT NULL,
                             `file_type` varchar(255) DEFAULT NULL,
                             `raw_content` mediumtext,
                             `stored_file_name` varchar(255) DEFAULT NULL,
                             `total_chunks` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `document_chunks`
--

CREATE TABLE `document_chunks` (
                                   `id` bigint NOT NULL,
                                   `chunk_index` int NOT NULL,
                                   `content` mediumtext,
                                   `embedding` longtext,
                                   `document_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
                          `id` bigint NOT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          `description` mediumtext,
                          `end_time` datetime(6) NOT NULL,
                          `is_active` bit(1) NOT NULL,
                          `is_published` bit(1) NOT NULL,
                          `location` varchar(255) NOT NULL,
                          `name` varchar(255) NOT NULL,
                          `permit_issued_at` datetime(6) NOT NULL,
                          `permit_issued_by` varchar(255) NOT NULL,
                          `permit_number` varchar(255) NOT NULL,
                          `start_time` datetime(6) NOT NULL,
                          `status` enum('COMPLETED','ONGOING','UPCOMING') DEFAULT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          `updated_by` varchar(255) DEFAULT NULL,
                          `genre_id` bigint DEFAULT NULL,
                          `producer_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `created_at`, `created_by`, `description`, `end_time`, `is_active`, `is_published`, `location`, `name`, `permit_issued_at`, `permit_issued_by`, `permit_number`, `start_time`, `status`, `updated_at`, `updated_by`, `genre_id`, `producer_id`) VALUES
                                                                                                                                                                                                                                                                                (1, '2026-04-17 05:50:01.203301', 'sukien@gmail.com', '<p class=\"ql-align-center\"><a href=\"https://salt.tkbcdn.com/ts/ds/3f/ef/4b/d0b0f434c0e9d744c080d55cf3bc167e.jpg\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(0, 0, 0);\"><strong><u>​</u></strong><u>GHI CHÚ:</u>&nbsp;Chỗ đặt sẽ được sắp xếp tự động theo thứ tự thanh toán (Một vài vị trí trung tâm sẽ hơi vướng cột)</a></p><p class=\"ql-align-center\"><a href=\"https://salt.tkbcdn.com/ts/ds/3f/ef/4b/d0b0f434c0e9d744c080d55cf3bc167e.jpg\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: inherit;\">Phòng trà Bến Thành&nbsp;có phục vụ F&amp;B, vui lòng không mang đồ ăn thức uống từ ngoài vào</a></p><p><a href=\"https://salt.tkbcdn.com/ts/ds/3f/ef/4b/d0b0f434c0e9d744c080d55cf3bc167e.jpg\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: inherit;\">- Phòng trà có phục vụ nước uống,quý khách vui lòng không mang thức ăn và nước uống từ bên ngoài vào phòng trà.</a></p><p><a href=\"https://salt.tkbcdn.com/ts/ds/3f/ef/4b/d0b0f434c0e9d744c080d55cf3bc167e.jpg\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: inherit;\">- Quý khách không gọi nước phòng trà sẽ tính phí phụ thu là 80.000VND/người.</a></p><p>- Một vài vị trí trung tâm sẽ hơi vướng cột.</p><p>- Chỗ đặt sẽ được xếp tự động theo thứ tự thanh toán.</p>', '2026-04-25 15:00:00.000000', b'1', b'1', 'Lầu 3, Nhà hát Bến Thành, Số 6 Mạc Đĩnh Chi , Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh', '[BẾN THÀNH] Đêm nhạc Hoài Lâm', '2026-03-31 17:00:00.000000', 'SVH', '123', '2026-04-25 13:00:00.000000', 'UPCOMING', '2026-04-17 05:57:02.918271', 'sukien@gmail.com', 1, 1),
                                                                                                                                                                                                                                                                                (2, '2026-04-17 05:56:18.580333', 'sukien@gmail.com', '<p>Bùi Lan Hương</p>', '2026-04-26 15:00:00.000000', b'1', b'1', 'Lầu 1, Nhà hát Bến Thành, Số 6 Mạc Đĩnh Chi, Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh', 'Bùi Lan Hương FANCON', '2026-04-04 17:00:00.000000', 'SVH', '123', '2026-04-26 12:00:00.000000', 'UPCOMING', '2026-04-17 05:57:01.798502', 'sukien@gmail.com', 1, 2),
                                                                                                                                                                                                                                                                                (3, '2026-04-17 06:02:58.310600', 'sukien@gmail.com', '<p><strong>[BẾN THÀNH] Đêm nhạc Minh Tuyết - Hoàng Hải</strong></p><p><br></p>', '2026-04-28 15:00:00.000000', b'1', b'1', 'Lầu 3, Nhà hát Bến Thành, Số 6 Mạc Đĩnh Chi, Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh', '[BẾN THÀNH] Đêm nhạc Minh Tuyết - Hoàng Hải', '2026-04-12 17:00:00.000000', 'SVH', '123', '2026-04-28 13:00:00.000000', 'UPCOMING', '2026-04-17 06:09:10.503641', 'sukien@gmail.com', 1, 3),
                                                                                                                                                                                                                                                                                (4, '2026-04-17 06:08:42.767578', 'sukien@gmail.com', '<p>Trung Quân</p>', '2026-05-02 16:00:00.000000', b'1', b'1', 'Cat&Mouse Live Music, 37B Phạm Ngọc Thạch, Phường 13, Quận Phú Nhuận, Thành phố Hồ Chí Minh', '[CAT&MOUSE] CA SĨ TRUNG QUÂN + CA SĨ MYRA TRẦN', '2026-04-11 17:00:00.000000', 'SVH', '123', '2026-05-02 13:00:00.000000', 'UPCOMING', '2026-04-17 06:09:08.960970', 'sukien@gmail.com', 1, 4),
                                                                                                                                                                                                                                                                                (5, '2026-04-17 06:17:33.147331', 'sukien@gmail.com', '<p><strong>Nhà Hát Kịch IDECAF: Một Ngày Làm VUA</strong></p><p><br></p><p><br></p>', '2026-04-25 15:30:00.000000', b'1', b'1', 'Sân khấu kịch Idecaf, Số 28 Lê Thánh Tôn, Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh', 'Nhà Hát Kịch IDECAF: Một Ngày Làm VUA', '2026-04-12 17:00:00.000000', 'SVH', '123', '2026-04-25 12:30:00.000000', 'UPCOMING', '2026-04-17 06:20:22.970625', 'sukien@gmail.com', 2, 5),
                                                                                                                                                                                                                                                                                (6, '2026-04-17 06:28:54.977777', 'sukien@gmail.com', '<p><strong>Nhà Hát Kịch IDECAF: TẤM CÁM ĐẠI CHIẾN!</strong></p><p><br></p><p><br></p>', '2026-04-30 15:00:00.000000', b'1', b'1', 'Nhà Hát Kịch IDECAF, Số 28 Lê Thánh Tôn, Phường Tân Định, Quận 1, Thành phố Hồ Chí Minh', 'Nhà Hát Kịch IDECAF: TẤM CÁM ĐẠI CHIẾN!', '2026-03-31 17:00:00.000000', 'SVH', '123', '2026-04-30 13:00:00.000000', 'UPCOMING', '2026-04-17 06:36:40.360020', 'sukien@gmail.com', 2, 6),
                                                                                                                                                                                                                                                                                (7, '2026-04-17 06:33:20.919596', 'sukien@gmail.com', '<p><strong>[GARDEN ART] - ART WORKSHOP \"CARROT CAKE\"</strong></p><p><br></p><p><br></p>', '2026-05-13 14:00:00.000000', b'1', b'1', '123, 1231, Phường Xuân La, Quận Tây Hồ, Thành phố Hà Nội', '[GARDEN ART] - ART WORKSHOP \"CARROT CAKE\"', '2026-04-12 17:00:00.000000', 'SVH', '123', '2026-05-13 03:00:00.000000', 'UPCOMING', '2026-04-17 06:36:37.641785', 'sukien@gmail.com', 2, 7),
                                                                                                                                                                                                                                                                                (8, '2026-04-17 06:36:12.974762', 'sukien@gmail.com', '<p><strong>ART WORKSHOP \"STRAWBERRY DOUBLE FROMAGE CHEESECAKE\"</strong></p><p><br></p><p><br></p>', '2026-05-06 14:00:00.000000', b'1', b'1', 'Garden Art, Lầu 1, 386/17C Lê Văn Sỹ, Phường 14, Quận 3, Thành phố Hồ Chí Minh', 'ART WORKSHOP \"STRAWBERRY DOUBLE FROMAGE CHEESECAKE\"', '2026-04-15 17:00:00.000000', 'SVH', '123', '2026-05-06 03:00:00.000000', 'UPCOMING', '2026-04-17 06:36:39.291816', 'sukien@gmail.com', 2, 8),
                                                                                                                                                                                                                                                                                (9, '2026-04-17 06:41:18.171292', 'sukien@gmail.com', '<p><strong>ART WORKSHOP \"BLUSH &amp; BERRIES CHARLOTTE\"</strong></p><p><br></p><p><br></p>', '2026-05-18 04:00:00.000000', b'1', b'1', 'Garden Art, Lầu 1, 386/17C Lê Văn Sỹ, Phường 14, Quận 3, Thành phố Hồ Chí Minh', 'ART WORKSHOP \"BLUSH & BERRIES CHARLOTTE\"', '2026-04-04 17:00:00.000000', 'SVH', '123', '2026-05-18 01:00:00.000000', 'UPCOMING', '2026-04-17 06:52:34.651849', 'sukien@gmail.com', 4, 9),
                                                                                                                                                                                                                                                                                (10, '2026-04-17 06:46:25.015298', 'sukien@gmail.com', '<p><strong>[VIVIAN VU’S CANDLES] WORKSHOP LÀM NẾN THƠM VÀ SÁP THƠM HANDMADE</strong></p><p><br></p><p><br></p>', '2026-06-08 09:00:00.000000', b'1', b'1', 'Tòa CDC, 25 Lê Đại Hành, Hai Bà Trưng, Hà Nội,, Phường Lê Đại Hành, Quận Hai Bà Trưng, Thành phố Hà Nội', '[VIVIAN VU’S CANDLES] WORKSHOP LÀM NẾN THƠM VÀ SÁP THƠM HANDMADE', '2026-04-11 17:00:00.000000', 'SVH', '123', '2026-06-08 07:00:00.000000', 'UPCOMING', '2026-04-17 06:52:31.828567', 'sukien@gmail.com', 4, 10),
                                                                                                                                                                                                                                                                                (11, '2026-04-17 06:49:16.771551', 'sukien@gmail.com', '<p><strong>[GARDEN ART] - ART WORKSHOP VẼ TRANH MÀU NƯỚC \"HOA TRONG VƯỜN\"</strong></p><p><br></p><p><br></p>', '2026-05-20 07:00:00.000000', b'1', b'1', 'Garden Art, Lầu 1, 386/17C Lê Văn Sỹ, Phường 14, Quận 3, Thành phố Hồ Chí Minh', '[GARDEN ART] - ART WORKSHOP VẼ TRANH MÀU NƯỚC \"HOA TRONG VƯỜN\"', '2026-04-13 17:00:00.000000', '123', '123', '2026-05-20 02:00:00.000000', 'UPCOMING', '2026-04-17 06:52:33.107091', 'sukien@gmail.com', 4, 11),
                                                                                                                                                                                                                                                                                (12, '2026-04-17 06:52:12.020835', 'sukien@gmail.com', '<p><strong>[GARDEN ART] - ART WORKSHOP \"TERRARIUM CAKE\"</strong></p><p><br></p><p><br></p>', '2026-05-21 05:00:00.000000', b'1', b'1', 'Garden Art, Lầu 1, 386/17C Lê Văn Sỹ, Phường 14, Quận 3, Thành phố Hồ Chí Minh', '[GARDEN ART] - ART WORKSHOP \"TERRARIUM CAKE\"', '2026-04-12 17:00:00.000000', '123', '123', '2026-05-21 02:00:00.000000', 'UPCOMING', '2026-04-17 06:52:35.873437', 'sukien@gmail.com', 4, 12),
                                                                                                                                                                                                                                                                                (13, '2026-04-17 07:00:17.268618', 'sukien@gmail.com', '<p><strong>Aquafield Ocean City</strong></p><p><br></p><p><br></p>', '2026-10-22 16:59:00.000000', b'1', b'1', 'Oceanpark 2, Tầng 2, Vincom Mega Mall, đường Tôn Quyền, Xã Nghĩa Trụ, Huyện Văn Giang, Tỉnh Hưng Yên', 'Aquafield Ocean City', '2026-03-31 17:00:00.000000', 'SVH', '123', '2026-10-21 17:00:00.000000', 'UPCOMING', '2026-04-17 07:13:11.775757', 'sukien@gmail.com', 5, 13),
                                                                                                                                                                                                                                                                                (14, '2026-04-17 07:03:21.404812', 'sukien@gmail.com', '<p><strong>Vé Trải Nghiệm KidZania Hà Nội</strong></p><p><br></p><p><br></p>', '2026-05-15 12:00:00.000000', b'1', b'1', 'KidZania Hà Nội, TTTM Lotte Mall Tây Hồ, 272 đường Võ Chí Công, Phường Phú Thượng, Quận Tây Hồ, Thành phố Hà Nội', 'Vé Trải Nghiệm KidZania Hà Nội', '2026-04-04 17:00:00.000000', 'SVH', '123', '2026-05-15 02:00:00.000000', 'UPCOMING', '2026-04-17 07:13:14.416833', 'sukien@gmail.com', 5, 14),
                                                                                                                                                                                                                                                                                (15, '2026-04-17 07:07:00.429484', 'sukien@gmail.com', '<p><strong>Trải Nghiệm Bay Dù Lượn Hà Nội</strong></p><p><br></p><p><br></p>', '2026-05-17 09:00:00.000000', b'1', b'1', 'Núi Bé, Nam Phương Tiến, Chương Mỹ, Hà Nội, Thôn Núi Bé, Xã Nam Phương Tiến, Huyện Chương Mỹ, Thành phố Hà Nội', 'Trải Nghiệm Bay Dù Lượn Hà Nội', '2026-04-04 17:00:00.000000', 'SVH', '123', '2026-05-17 01:00:00.000000', 'UPCOMING', '2026-04-17 07:13:15.502850', 'sukien@gmail.com', 5, 15),
                                                                                                                                                                                                                                                                                (16, '2026-04-17 07:12:03.712411', 'sukien@gmail.com', '<p><strong>Trải Nghiệm Bay Dù Lượn Mù Cang Chải</strong></p><p><br></p><p><br></p>', '2026-05-01 09:00:00.000000', b'1', b'1', 'Đèo Khau Phạ| Mù Cang Chải, Đèo Khau Phạ, Xã Cao Phạ, Huyện Mù Căng Chải, Tỉnh Yên Bái', 'Trải Nghiệm Bay Dù Lượn Mù Cang Chải', '2026-04-07 17:00:00.000000', 'SVH', '123', '2026-05-01 01:00:00.000000', 'UPCOMING', '2026-04-17 07:13:20.174106', 'sukien@gmail.com', 5, 16);

-- --------------------------------------------------------

--
-- Table structure for table `event_artists`
--

CREATE TABLE `event_artists` (
                                 `event_id` bigint NOT NULL,
                                 `artist_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `event_artists`
--

INSERT INTO `event_artists` (`event_id`, `artist_name`) VALUES
                                                            (1, 'Hoài Lâm'),
                                                            (2, 'Bùi Lan Hương'),
                                                            (3, 'Minh Tuyết'),
                                                            (3, 'Hoàng Hải'),
                                                            (4, 'Myra Trần'),
                                                            (4, 'Trung Quân'),
                                                            (5, 'Trúc My'),
                                                            (5, 'Quốc Tuấn'),
                                                            (5, 'Hồng Ánh'),
                                                            (5, 'Đại Nghĩa'),
                                                            (5, 'Quốc Thịnh'),
                                                            (5, 'Đình Toàn'),
                                                            (5, 'Phước Lộc'),
                                                            (5, 'Bảo Cường'),
                                                            (5, 'Thái Hiển'),
                                                            (5, 'Mỹ Duyên'),
                                                            (5, 'Bạch Long'),
                                                            (5, 'Hoài Trang'),
                                                            (5, 'Cẩm Hò'),
                                                            (5, 'Tâm Anh'),
                                                            (5, 'Quang Thảo'),
                                                            (5, 'Phạm Hạnh'),
                                                            (5, 'Bích Trâm'),
                                                            (6, 'Đại Nghĩa'),
                                                            (6, 'Đình Toàn'),
                                                            (6, 'Đông Hải'),
                                                            (6, 'Mai Phượng'),
                                                            (6, 'Mỹ Duyên'),
                                                            (6, 'Hòa Hiệp'),
                                                            (6, 'Bạch Long'),
                                                            (6, 'Cẩm Hò'),
                                                            (6, 'Thiên Trang'),
                                                            (6, 'Tâm Anh'),
                                                            (6, 'Việt Trang và Nhà Hát Thiếu Nhi Nụ Cười'),
                                                            (6, 'Trịnh Minh Dũng'),
                                                            (6, 'Tuyền Mập');

-- --------------------------------------------------------

--
-- Table structure for table `event_image`
--

CREATE TABLE `event_image` (
                               `id` bigint NOT NULL,
                               `created_at` datetime(6) DEFAULT NULL,
                               `created_by` varchar(255) DEFAULT NULL,
                               `is_cover` bit(1) NOT NULL,
                               `updated_at` datetime(6) DEFAULT NULL,
                               `updated_by` varchar(255) DEFAULT NULL,
                               `url` varchar(255) DEFAULT NULL,
                               `event_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `event_image`
--

INSERT INTO `event_image` (`id`, `created_at`, `created_by`, `is_cover`, `updated_at`, `updated_by`, `url`, `event_id`) VALUES
                                                                                                                            (1, '2026-04-17 05:50:01.592673', 'sukien@gmail.com', b'1', NULL, NULL, '1776405001578-HoaiLam.jpg', 1),
                                                                                                                            (2, '2026-04-17 05:50:01.599486', 'sukien@gmail.com', b'0', NULL, NULL, '1776405001599-BenThanh.jpg', 1),
                                                                                                                            (3, '2026-04-17 05:56:18.645616', 'sukien@gmail.com', b'1', NULL, NULL, '1776405378642-BuiLanHuong.jpg', 2),
                                                                                                                            (4, '2026-04-17 05:56:18.645616', 'sukien@gmail.com', b'0', NULL, NULL, '1776405378645-BenThanh.jpg', 2),
                                                                                                                            (5, '2026-04-17 06:02:58.818587', 'sukien@gmail.com', b'1', NULL, NULL, '1776405778816-MinhTuyet.jpg', 3),
                                                                                                                            (6, '2026-04-17 06:02:58.824578', 'sukien@gmail.com', b'0', NULL, NULL, '1776405778818-BenThanh.jpg', 3),
                                                                                                                            (7, '2026-04-17 06:08:42.836925', 'sukien@gmail.com', b'1', NULL, NULL, '1776406122834-TrungQuan.png', 4),
                                                                                                                            (8, '2026-04-17 06:08:42.836925', 'sukien@gmail.com', b'0', NULL, NULL, '1776406122836-BenThanh.jpg', 4),
                                                                                                                            (9, '2026-04-17 06:17:33.598658', 'sukien@gmail.com', b'1', NULL, NULL, '1776406653591-1.jpg', 5),
                                                                                                                            (10, '2026-04-17 06:17:33.610256', 'sukien@gmail.com', b'0', NULL, NULL, '1776406653609-idecaf.png', 5),
                                                                                                                            (11, '2026-04-17 06:28:55.459400', 'sukien@gmail.com', b'1', NULL, NULL, '1776407335443-tamcam.jpg', 6),
                                                                                                                            (12, '2026-04-17 06:28:55.463904', 'sukien@gmail.com', b'0', NULL, NULL, '1776407335463-idecaf.png', 6),
                                                                                                                            (13, '2026-04-17 06:33:20.990737', 'sukien@gmail.com', b'1', NULL, NULL, '1776407600983-carrot_cake.jpg', 7),
                                                                                                                            (14, '2026-04-17 06:33:20.994373', 'sukien@gmail.com', b'0', NULL, NULL, '1776407600992-gardenArt.png', 7),
                                                                                                                            (15, '2026-04-17 06:36:13.038735', 'sukien@gmail.com', b'1', NULL, NULL, '1776407773035-chese_cake.jpg', 8),
                                                                                                                            (16, '2026-04-17 06:36:13.045547', 'sukien@gmail.com', b'0', NULL, NULL, '1776407773043-gardenArt.png', 8),
                                                                                                                            (17, '2026-04-17 06:41:18.225302', 'sukien@gmail.com', b'1', NULL, NULL, '1776408078217-charlotte.jpg', 9),
                                                                                                                            (18, '2026-04-17 06:41:18.228597', 'sukien@gmail.com', b'0', NULL, NULL, '1776408078227-gardenArt.png', 9),
                                                                                                                            (19, '2026-04-17 06:46:25.075211', 'sukien@gmail.com', b'1', NULL, NULL, '1776408385064-nen.jpg', 10),
                                                                                                                            (20, '2026-04-17 06:46:25.079818', 'sukien@gmail.com', b'0', NULL, NULL, '1776408385077-275x275.png', 10),
                                                                                                                            (21, '2026-04-17 06:49:16.827188', 'sukien@gmail.com', b'1', NULL, NULL, '1776408556820-hoa_trong_vuon.jpg', 11),
                                                                                                                            (22, '2026-04-17 06:49:16.830271', 'sukien@gmail.com', b'0', NULL, NULL, '1776408556829-gardenArt.png', 11),
                                                                                                                            (23, '2026-04-17 06:52:12.400267', 'sukien@gmail.com', b'1', NULL, NULL, '1776408732367-terrauim_cake.jpg', 12),
                                                                                                                            (24, '2026-04-17 06:52:12.402682', 'sukien@gmail.com', b'0', NULL, NULL, '1776408732402-gardenArt.png', 12),
                                                                                                                            (25, '2026-04-17 07:00:17.338224', 'sukien@gmail.com', b'1', NULL, NULL, '1776409217336-spa.jpg', 13),
                                                                                                                            (26, '2026-04-17 07:00:17.346166', 'sukien@gmail.com', b'0', NULL, NULL, '1776409217338-city.jpg', 13),
                                                                                                                            (27, '2026-04-17 07:03:21.455474', 'sukien@gmail.com', b'1', NULL, NULL, '1776409401455-kidzania.jpg', 14),
                                                                                                                            (28, '2026-04-17 07:03:21.464007', 'sukien@gmail.com', b'0', NULL, NULL, '1776409401455-zaniahanoi.jpg', 14),
                                                                                                                            (29, '2026-04-17 07:07:00.482561', 'sukien@gmail.com', b'1', NULL, NULL, '1776409620482-du_luon.jpg', 15),
                                                                                                                            (30, '2026-04-17 07:07:00.482561', 'sukien@gmail.com', b'0', NULL, NULL, '1776409620482-mebay.png', 15),
                                                                                                                            (31, '2026-04-17 07:12:03.810117', 'sukien@gmail.com', b'1', NULL, NULL, '1776409923800-chang_hai.jpg', 16),
                                                                                                                            (32, '2026-04-17 07:12:03.812870', 'sukien@gmail.com', b'0', NULL, NULL, '1776409923811-mebay.png', 16);

-- --------------------------------------------------------

--
-- Table structure for table `event_staffs`
--

CREATE TABLE `event_staffs` (
                                `id` bigint NOT NULL,
                                `created_at` datetime(6) DEFAULT NULL,
                                `created_by` varchar(255) DEFAULT NULL,
                                `event_id` bigint DEFAULT NULL,
                                `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `event_staffs`
--

INSERT INTO `event_staffs` (`id`, `created_at`, `created_by`, `event_id`, `user_id`) VALUES
    (1, '2026-04-20 04:47:38.023203', 'sukien@gmail.com', 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `genres`
--

CREATE TABLE `genres` (
                          `id` bigint NOT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          `name` varchar(255) NOT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `genres`
--

INSERT INTO `genres` (`id`, `created_at`, `created_by`, `name`, `updated_at`, `updated_by`) VALUES
                                                                                                (1, '2026-04-01 23:11:07.000000', 'system', 'Nhạc sống', NULL, NULL),
                                                                                                (2, '2026-04-01 23:11:07.000000', 'system', 'Sân khấu và Nghệ thuật', NULL, NULL),
                                                                                                (4, '2026-04-01 23:11:07.000000', 'system', 'Hội thảo và Workshop', NULL, NULL),
                                                                                                (5, '2026-04-01 23:11:07.000000', 'system', 'Tham quan và Trải nghiệm', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
                          `id` bigint NOT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          `order_code` varchar(255) DEFAULT NULL,
                          `order_status` enum('CANCELLED','PAID','PENDING','REFUNDED') DEFAULT NULL,
                          `paid_at` datetime(6) DEFAULT NULL,
                          `total_amount` double NOT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          `updated_by` varchar(255) DEFAULT NULL,
                          `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `created_at`, `created_by`, `order_code`, `order_status`, `paid_at`, `total_amount`, `updated_at`, `updated_by`, `user_id`) VALUES
                                                                                                                                                            (1, '2026-04-19 17:52:05.919865', '1@gmail.com', 'ORD-20260420-DE99CDDF', 'CANCELLED', NULL, 500000, '2026-04-19 17:52:50.880767', '1@gmail.com', 3),
                                                                                                                                                            (2, '2026-04-20 02:55:30.742265', '1@gmail.com', 'ORD-20260420-0AE76C2E', 'CANCELLED', NULL, 789000, '2026-04-20 02:55:36.150982', '1@gmail.com', 3),
                                                                                                                                                            (3, '2026-04-20 03:12:55.863634', '1@gmail.com', 'ORD-20260420-FB808FF6', 'CANCELLED', NULL, 500000, '2026-04-20 03:13:06.987708', '1@gmail.com', 3),
                                                                                                                                                            (4, '2026-04-20 03:15:44.689447', '1@gmail.com', 'ORD-20260420-599C8081', 'CANCELLED', NULL, 500000, '2026-04-20 03:26:32.323704', '1@gmail.com', 3),
                                                                                                                                                            (5, '2026-04-20 03:33:43.779908', '1@gmail.com', 'ORD-20260420-D7C5C98D', 'CANCELLED', NULL, 500000, '2026-04-20 03:33:47.328719', '1@gmail.com', 3),
                                                                                                                                                            (8, '2026-04-20 03:46:53.497043', '1@gmail.com', 'ORD-20260420-EBAC861D', 'CANCELLED', NULL, 500000, '2026-04-20 03:47:00.724074', '1@gmail.com', 3),
                                                                                                                                                            (9, '2026-04-20 03:47:52.538431', '1@gmail.com', 'ORD-20260420-C34F8B18', 'CANCELLED', NULL, 500000, '2026-04-20 03:47:53.655044', '1@gmail.com', 3),
                                                                                                                                                            (10, '2026-04-20 03:48:12.805545', '1@gmail.com', 'ORD-20260420-2C4596FF', 'CANCELLED', NULL, 500000, '2026-04-20 03:48:13.906779', '1@gmail.com', 3),
                                                                                                                                                            (11, '2026-04-20 03:48:27.050401', '1@gmail.com', 'ORD-20260420-4767FAD1', 'CANCELLED', NULL, 500000, '2026-04-20 03:48:28.161373', '1@gmail.com', 3),
                                                                                                                                                            (12, '2026-04-20 03:48:44.939428', '1@gmail.com', 'ORD-20260420-AC753387', 'CANCELLED', NULL, 500000, '2026-04-20 03:48:46.034162', '1@gmail.com', 3),
                                                                                                                                                            (13, '2026-04-20 03:50:34.082202', '1@gmail.com', 'ORD-20260420-4E4703BA', 'CANCELLED', NULL, 500000, '2026-04-20 03:50:35.197486', '1@gmail.com', 3),
                                                                                                                                                            (14, '2026-04-20 03:51:10.940166', '1@gmail.com', 'ORD-20260420-1C2263DB', 'CANCELLED', NULL, 500000, '2026-04-20 03:51:17.484651', '1@gmail.com', 3),
                                                                                                                                                            (15, '2026-04-20 03:51:33.481079', '1@gmail.com', 'ORD-20260420-F1FCA472', 'CANCELLED', NULL, 500000, '2026-04-20 03:51:34.617137', '1@gmail.com', 3),
                                                                                                                                                            (16, '2026-04-20 03:52:29.273187', '1@gmail.com', 'ORD-20260420-58EA7D4B', 'CANCELLED', NULL, 500000, '2026-04-20 03:52:30.368357', '1@gmail.com', 3),
                                                                                                                                                            (17, '2026-04-20 03:54:26.709736', '1@gmail.com', 'ORD-20260420-A120A994', 'CANCELLED', NULL, 500000, '2026-04-20 03:54:27.851818', '1@gmail.com', 3),
                                                                                                                                                            (18, '2026-04-20 03:58:59.979899', '1@gmail.com', 'ORD-20260420-5C103FCD', 'CANCELLED', NULL, 500000, '2026-04-20 03:59:01.179975', '1@gmail.com', 3),
                                                                                                                                                            (19, '2026-04-20 03:59:25.970634', '1@gmail.com', 'ORD-20260420-870F1A30', 'CANCELLED', NULL, 1500000, '2026-04-20 03:59:31.312729', '1@gmail.com', 3),
                                                                                                                                                            (20, '2026-04-20 03:59:34.381115', '1@gmail.com', 'ORD-20260420-6F62DABB', 'CANCELLED', NULL, 500000, '2026-04-20 03:59:38.231549', '1@gmail.com', 3),
                                                                                                                                                            (21, '2026-04-20 03:59:45.938579', '1@gmail.com', 'ORD-20260420-8F362D9A', 'PAID', '2026-04-20 04:00:02.830830', 500000, '2026-04-20 04:00:02.831831', '1@gmail.com', 3),
                                                                                                                                                            (22, '2026-04-20 04:00:30.022787', '1@gmail.com', 'ORD-20260420-9DC66C92', 'CANCELLED', NULL, 1500000, '2026-04-20 04:00:32.722379', '1@gmail.com', 3);

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
                               `id` bigint NOT NULL,
                               `price_per_unit` double NOT NULL,
                               `quantity` int NOT NULL,
                               `subtotal` double NOT NULL,
                               `order_id` bigint DEFAULT NULL,
                               `seat_id` bigint DEFAULT NULL,
                               `ticket_id` bigint DEFAULT NULL
) ;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `price_per_unit`, `quantity`, `subtotal`, `order_id`, `seat_id`, `ticket_id`) VALUES
                                                                                                                   (1, 500000, 1, 500000, 1, 8, 5),
                                                                                                                   (2, 789000, 1, 789000, 2, NULL, 3),
                                                                                                                   (3, 500000, 1, 500000, 3, 17, 5),
                                                                                                                   (4, 500000, 1, 500000, 4, 15, 5),
                                                                                                                   (5, 500000, 1, 500000, 5, 13, 5),
                                                                                                                   (8, 500000, 1, 500000, 8, 17, 5),
                                                                                                                   (9, 500000, 1, 500000, 9, 12, 5),
                                                                                                                   (10, 500000, 1, 500000, 10, 12, 5),
                                                                                                                   (11, 500000, 1, 500000, 11, 10, 5),
                                                                                                                   (12, 500000, 1, 500000, 12, 16, 5),
                                                                                                                   (13, 500000, 1, 500000, 13, 17, 5),
                                                                                                                   (14, 500000, 1, 500000, 14, 15, 5),
                                                                                                                   (15, 500000, 1, 500000, 15, 23, 5),
                                                                                                                   (16, 500000, 1, 500000, 16, 15, 5),
                                                                                                                   (17, 500000, 1, 500000, 17, 8, 5),
                                                                                                                   (18, 500000, 1, 500000, 18, 15, 5),
                                                                                                                   (19, 1500000, 1, 1500000, 19, 36, 6),
                                                                                                                   (20, 500000, 1, 500000, 20, 13, 5),
                                                                                                                   (21, 500000, 1, 500000, 21, 7, 5),
                                                                                                                   (22, 1500000, 1, 1500000, 22, 35, 6);

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
                               `id` bigint NOT NULL,
                               `api_path` varchar(255) NOT NULL,
                               `created_at` datetime(6) DEFAULT NULL,
                               `created_by` varchar(255) DEFAULT NULL,
                               `method` varchar(255) NOT NULL,
                               `module` varchar(255) NOT NULL,
                               `name` varchar(255) NOT NULL,
                               `updated_at` datetime(6) DEFAULT NULL,
                               `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `permissions`
--

INSERT INTO `permissions` (`id`, `api_path`, `created_at`, `created_by`, `method`, `module`, `name`, `updated_at`, `updated_by`) VALUES
                                                                                                                                     (1, '/api/v1/permissions', '2026-04-17 05:12:27.691511', '', 'POST', 'PERMISSIONS', 'Create a permission', NULL, NULL),
                                                                                                                                     (2, '/api/v1/permissions', '2026-04-17 05:12:27.745571', '', 'PUT', 'PERMISSIONS', 'Update a permission', NULL, NULL),
                                                                                                                                     (3, '/api/v1/permissions/{id}', '2026-04-17 05:12:27.747571', '', 'DELETE', 'PERMISSIONS', 'Delete a permission', NULL, NULL),
                                                                                                                                     (4, '/api/v1/permissions/{id}', '2026-04-17 05:12:27.749572', '', 'GET', 'PERMISSIONS', 'Get a permission by id', NULL, NULL),
                                                                                                                                     (5, '/api/v1/permissions', '2026-04-17 05:12:27.750571', '', 'GET', 'PERMISSIONS', 'Get permissions with pagination', NULL, NULL),
                                                                                                                                     (6, '/api/v1/roles', '2026-04-17 05:12:27.751572', '', 'POST', 'ROLES', 'Create a role', NULL, NULL),
                                                                                                                                     (7, '/api/v1/roles', '2026-04-17 05:12:27.752572', '', 'PUT', 'ROLES', 'Update a role', NULL, NULL),
                                                                                                                                     (8, '/api/v1/roles/{id}', '2026-04-17 05:12:27.754573', '', 'DELETE', 'ROLES', 'Delete a role', NULL, NULL),
                                                                                                                                     (9, '/api/v1/roles/{id}', '2026-04-17 05:12:27.755572', '', 'GET', 'ROLES', 'Get a role by id', NULL, NULL),
                                                                                                                                     (10, '/api/v1/roles', '2026-04-17 05:12:27.756572', '', 'GET', 'ROLES', 'Get roles with pagination', NULL, NULL),
                                                                                                                                     (11, '/api/v1/users', '2026-04-17 05:12:27.757572', '', 'POST', 'USERS', 'Create a user', NULL, NULL),
                                                                                                                                     (12, '/api/v1/users/{id}', '2026-04-17 05:12:27.758572', '', 'DELETE', 'USERS', 'Delete a user', NULL, NULL),
                                                                                                                                     (13, '/api/v1/users', '2026-04-17 05:12:27.759572', '', 'GET', 'USERS', 'Get users with pagination', NULL, NULL),
                                                                                                                                     (14, '/api/v1/users', '2026-04-17 05:12:27.760572', '', 'PUT', 'USERS', 'Update a user', NULL, NULL),
                                                                                                                                     (15, '/api/v1/users/{id}', '2026-04-17 05:12:27.761573', '', 'GET', 'USERS', 'Get a user by id', NULL, NULL),
                                                                                                                                     (16, '/api/v1/genres', '2026-04-17 05:12:27.762572', '', 'POST', 'GENRES', 'Create a Genre', NULL, NULL),
                                                                                                                                     (17, '/api/v1/genres', '2026-04-17 05:12:27.763572', '', 'PUT', 'GENRES', 'Update a Genre', NULL, NULL),
                                                                                                                                     (18, '/api/v1/genres/{id}', '2026-04-17 05:12:27.764572', '', 'DELETE', 'GENRES', 'Delete a Genre', NULL, NULL),
                                                                                                                                     (19, '/api/v1/events', '2026-04-17 05:12:27.766573', '', 'POST', 'EVENTS', 'Create a Event', NULL, NULL),
                                                                                                                                     (20, '/api/v1/events', '2026-04-17 05:12:27.768573', '', 'PUT', 'EVENTS', 'Update a Event', NULL, NULL),
                                                                                                                                     (21, '/api/v1/events/{id}', '2026-04-17 05:12:27.769571', '', 'DELETE', 'EVENTS', 'Delete a Event', NULL, NULL),
                                                                                                                                     (22, '/api/v1/events/{id}/active', '2026-04-17 05:12:27.770574', '', 'PATCH', 'EVENTS', 'Toggle Active', NULL, NULL),
                                                                                                                                     (23, '/api/v1/events/{id}/published', '2026-04-17 05:12:27.772575', '', 'PATCH', 'EVENTS', 'Toggle Published', NULL, NULL),
                                                                                                                                     (24, '/api/v1/tickets', '2026-04-17 05:12:27.773573', '', 'POST', 'TICKETS', 'Create Tickets', NULL, NULL),
                                                                                                                                     (25, '/api/v1/tickets/{id}', '2026-04-17 05:12:27.775574', '', 'PUT', 'TICKETS', 'Update Tickets', NULL, NULL),
                                                                                                                                     (26, '/api/v1/orders', '2026-04-17 05:12:27.776573', '', 'POST', 'ORDERS', 'Create an order', NULL, NULL),
                                                                                                                                     (27, '/api/v1/orders/pay', '2026-04-17 05:12:27.777572', '', 'POST', 'ORDERS', 'Pay an order', NULL, NULL),
                                                                                                                                     (28, '/api/v1/orders/{id}/cancel', '2026-04-17 05:12:27.778572', '', 'POST', 'ORDERS', 'Cancel an order', NULL, NULL),
                                                                                                                                     (29, '/api/v1/orders/{id}', '2026-04-17 05:12:27.779572', '', 'GET', 'ORDERS', 'Get an order by id', NULL, NULL),
                                                                                                                                     (30, '/api/v1/orders', '2026-04-17 05:12:27.780571', '', 'GET', 'ORDERS', 'Get orders with pagination', NULL, NULL),
                                                                                                                                     (31, '/api/v1/orders/my-tickets', '2026-04-17 05:12:27.782576', '', 'GET', 'ORDERS', 'Get my tickets', NULL, NULL),
                                                                                                                                     (32, '/api/v1/orders/verify-qr', '2026-04-17 05:12:27.783572', '', 'POST', 'ORDERS', 'Verify QR Code', NULL, NULL),
                                                                                                                                     (33, '/api/v1/seats/recommend-adjacent', '2026-04-17 05:12:27.785571', '', 'POST', 'ORDERS', 'Recommend Adjacent Seats', NULL, NULL),
                                                                                                                                     (34, '/api/v1/events/{eventId}/images', '2026-04-17 05:12:27.786572', '', 'POST', 'FILES', 'Upload a file', NULL, NULL),
                                                                                                                                     (35, '/api/v1/events/{eventId}/images/{imageId}', '2026-04-17 05:12:27.787571', '', 'PUT', 'FILES', 'Update a file', NULL, NULL),
                                                                                                                                     (36, '/api/v1/events/{eventId}/images/{imageId}', '2026-04-17 05:12:27.788630', '', 'DELETE', 'FILES', 'Delete a file', NULL, NULL),
                                                                                                                                     (37, '/api/v1/producers', '2026-04-17 05:12:27.789572', '', 'POST', 'PRODUCER', 'Create Producer', NULL, NULL),
                                                                                                                                     (38, '/api/v1/producers/{id}', '2026-04-17 05:12:27.790570', '', 'PUT', 'PRODUCER', 'Update Producer', NULL, NULL),
                                                                                                                                     (39, '/api/v1/producers/{id}', '2026-04-17 05:12:27.791570', '', 'DELETE', 'PRODUCER', 'Delete Producer', NULL, NULL),
                                                                                                                                     (40, '/api/v1/producers', '2026-04-17 05:12:27.792570', '', 'GET', 'PRODUCER', 'Get All Producers', NULL, NULL),
                                                                                                                                     (41, '/api/v1/producers/{id}', '2026-04-17 05:12:27.793572', '', 'GET', 'PRODUCER', 'Get Producer by ID', NULL, NULL),
                                                                                                                                     (42, '/api/v1/seats', '2026-04-17 05:12:27.794571', '', 'POST', 'SEAT', 'Create Seat', NULL, NULL),
                                                                                                                                     (43, '/api/v1/seats/{id}', '2026-04-17 05:12:27.795571', '', 'PUT', 'SEAT', 'Update Seat', NULL, NULL),
                                                                                                                                     (44, '/api/v1/seats/{id}', '2026-04-17 05:12:27.797577', '', 'DELETE', 'SEAT', 'Delete Seat', NULL, NULL),
                                                                                                                                     (45, '/api/v1/seats/{id}', '2026-04-17 05:12:27.798572', '', 'GET', 'SEAT', 'Get Seat by ID', NULL, NULL),
                                                                                                                                     (46, '/api/v1/seats/event/{eventId}', '2026-04-17 05:12:27.799573', '', 'GET', 'SEAT', 'Get Seats by Event', NULL, NULL),
                                                                                                                                     (47, '/api/v1/transactions', '2026-04-17 05:12:27.800573', '', 'POST', 'TRANSACTION', 'Create Transaction', NULL, NULL),
                                                                                                                                     (48, '/api/v1/transactions/{id}', '2026-04-17 05:12:27.800573', '', 'PUT', 'TRANSACTION', 'Update Transaction', NULL, NULL),
                                                                                                                                     (49, '/api/v1/transactions', '2026-04-17 05:12:27.801573', '', 'GET', 'TRANSACTION', 'Get All Transactions', NULL, NULL),
                                                                                                                                     (50, '/api/v1/transactions/{id}', '2026-04-17 05:12:27.803573', '', 'GET', 'TRANSACTION', 'Get Transaction by ID', NULL, NULL),
                                                                                                                                     (51, '/api/v1/user-tickets', '2026-04-17 05:12:27.804573', '', 'POST', 'USER_TICKET', 'Create User Ticket', NULL, NULL),
                                                                                                                                     (52, '/api/v1/user-tickets/{id}', '2026-04-17 05:12:27.805573', '', 'PUT', 'USER_TICKET', 'Update User Ticket', NULL, NULL),
                                                                                                                                     (53, '/api/v1/user-tickets', '2026-04-17 05:12:27.806572', '', 'GET', 'USER_TICKET', 'Get All User Tickets', NULL, NULL),
                                                                                                                                     (54, '/api/v1/user-tickets/{id}', '2026-04-17 05:12:27.807572', '', 'GET', 'USER_TICKET', 'Get User Ticket by ID', NULL, NULL),
                                                                                                                                     (55, '/api/v1/user-tickets/user/{userId}', '2026-04-17 05:12:27.807572', '', 'GET', 'USER_TICKET', 'Get Tickets by User', NULL, NULL),
                                                                                                                                     (56, '/api/v1/event-staffs', '2026-04-17 05:12:27.808572', '', 'POST', 'EVENT_STAFF', 'Add Staff To Event', NULL, NULL),
                                                                                                                                     (57, '/api/v1/event-staffs/{id}', '2026-04-17 05:12:27.809572', '', 'DELETE', 'EVENT_STAFF', 'Remove Staff From Event', NULL, NULL),
                                                                                                                                     (58, '/api/v1/event-staffs/event/{eventId}', '2026-04-17 05:12:27.810628', '', 'GET', 'EVENT_STAFF', 'Get Staffs By Event', NULL, NULL),
                                                                                                                                     (59, '/api/v1/event-staffs/user/{userId}', '2026-04-17 05:12:27.811573', '', 'GET', 'EVENT_STAFF', 'Get Events By Staff', NULL, NULL),
                                                                                                                                     (79, '/api/v1/transactions', '2026-04-12 15:50:01.000000', NULL, 'POST', 'TRANSACTION', 'Quản lý giao dịch', NULL, NULL),
                                                                                                                                     (80, '/api/v1/transactions', NULL, NULL, 'GET', 'TRANSACTION', 'Xem danh sách giao dịch', NULL, NULL),
                                                                                                                                     (81, '/api/v1/transactions/{id}', NULL, NULL, 'GET', 'TRANSACTION', 'Xem chi tiết giao dịch', NULL, NULL),
                                                                                                                                     (82, '/api/v1/transactions', '2026-04-12 15:54:54.000000', NULL, 'POST', 'TRANSACTION', 'Tạo mới giao dịch', NULL, NULL),
                                                                                                                                     (83, '/api/v1/transactions', '2026-04-12 15:54:54.000000', NULL, 'GET', 'TRANSACTION', 'Lấy danh sách giao dịch', NULL, NULL),
                                                                                                                                     (84, '/api/v1/transactions/{id}', '2026-04-12 15:54:54.000000', NULL, 'GET', 'TRANSACTION', 'Xem chi tiết giao dịch', NULL, NULL),
                                                                                                                                     (85, '/api/v1/transactions/{id}', '2026-04-12 15:54:54.000000', NULL, 'PUT', 'TRANSACTION', 'Cập nhật giao dịch', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `permission_role`
--

CREATE TABLE `permission_role` (
                                   `role_id` bigint NOT NULL,
                                   `permission_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `permission_role`
--

INSERT INTO `permission_role` (`role_id`, `permission_id`) VALUES
                                                               (1, 1),
                                                               (1, 2),
                                                               (1, 3),
                                                               (1, 4),
                                                               (1, 5),
                                                               (1, 6),
                                                               (1, 7),
                                                               (1, 8),
                                                               (1, 9),
                                                               (1, 10),
                                                               (1, 11),
                                                               (1, 12),
                                                               (1, 13),
                                                               (1, 14),
                                                               (1, 15),
                                                               (1, 16),
                                                               (1, 17),
                                                               (1, 18),
                                                               (1, 19),
                                                               (1, 20),
                                                               (1, 21),
                                                               (1, 22),
                                                               (1, 23),
                                                               (1, 24),
                                                               (1, 25),
                                                               (1, 26),
                                                               (1, 27),
                                                               (1, 28),
                                                               (1, 29),
                                                               (1, 30),
                                                               (1, 31),
                                                               (1, 32),
                                                               (1, 33),
                                                               (1, 34),
                                                               (1, 35),
                                                               (1, 36),
                                                               (1, 37),
                                                               (1, 38),
                                                               (1, 39),
                                                               (1, 40),
                                                               (1, 41),
                                                               (1, 42),
                                                               (1, 43),
                                                               (1, 44),
                                                               (1, 45),
                                                               (1, 46),
                                                               (1, 47),
                                                               (1, 48),
                                                               (1, 49),
                                                               (1, 50),
                                                               (1, 51),
                                                               (1, 52),
                                                               (1, 53),
                                                               (1, 54),
                                                               (1, 55),
                                                               (1, 56),
                                                               (1, 57),
                                                               (1, 58),
                                                               (1, 59),
                                                               (2, 14),
                                                               (2, 15),
                                                               (2, 26),
                                                               (2, 27),
                                                               (2, 28),
                                                               (2, 29),
                                                               (2, 30),
                                                               (2, 31),
                                                               (2, 33),
                                                               (2, 46),
                                                               (3, 14),
                                                               (3, 15),
                                                               (3, 19),
                                                               (3, 20),
                                                               (3, 21),
                                                               (3, 22),
                                                               (3, 23),
                                                               (3, 24),
                                                               (3, 25),
                                                               (3, 34),
                                                               (3, 35),
                                                               (3, 36),
                                                               (3, 42),
                                                               (3, 43),
                                                               (3, 44),
                                                               (3, 45),
                                                               (3, 46),
                                                               (3, 56),
                                                               (3, 57),
                                                               (3, 58),
                                                               (3, 59),
                                                               (4, 14),
                                                               (4, 15),
                                                               (4, 32),
                                                               (1, 85),
                                                               (2, 44),
                                                               (2, 54),
                                                               (2, 45),
                                                               (2, 79),
                                                               (2, 82),
                                                               (3, 11),
                                                               (3, 13),
                                                               (3, 47),
                                                               (3, 48),
                                                               (3, 80),
                                                               (3, 81),
                                                               (3, 83),
                                                               (3, 84),
                                                               (4, 14),
                                                               (4, 15),
                                                               (4, 32),
                                                               (1, 79),
                                                               (1, 80),
                                                               (1, 81),
                                                               (1, 82),
                                                               (1, 83),
                                                               (1, 84),
                                                               (3, 29),
                                                               (3, 31),
                                                               (3, 32),
                                                               (3, 33),
                                                               (3, 40),
                                                               (3, 41),
                                                               (3, 55),
                                                               (4, 31),
                                                               (3, 30);

-- --------------------------------------------------------

--
-- Table structure for table `producers`
--

CREATE TABLE `producers` (
                             `id` bigint NOT NULL,
                             `bank_account_number` varchar(255) NOT NULL,
                             `bank_name` varchar(255) NOT NULL,
                             `contact_email` varchar(255) DEFAULT NULL,
                             `created_at` datetime(6) DEFAULT NULL,
                             `created_by` varchar(255) DEFAULT NULL,
                             `producer_name` varchar(255) NOT NULL,
                             `updated_at` datetime(6) DEFAULT NULL,
                             `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `producers`
--

INSERT INTO `producers` (`id`, `bank_account_number`, `bank_name`, `contact_email`, `created_at`, `created_by`, `producer_name`, `updated_at`, `updated_by`) VALUES
                                                                                                                                                                 (1, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 05:50:01.203301', 'sukien@gmail.com', 'Bến thành ', NULL, NULL),
                                                                                                                                                                 (2, '123', 'VIET', 'sukien@gmail.com', '2026-04-17 05:56:18.580333', 'sukien@gmail.com', 'BC', NULL, NULL),
                                                                                                                                                                 (3, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:02:58.310600', 'sukien@gmail.com', 'Bến thành ', NULL, NULL),
                                                                                                                                                                 (4, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:08:42.767578', 'sukien@gmail.com', 'BC', NULL, NULL),
                                                                                                                                                                 (5, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:17:33.156897', 'sukien@gmail.com', 'idecaf', NULL, NULL),
                                                                                                                                                                 (6, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:28:54.977777', 'sukien@gmail.com', 'idecaf', NULL, NULL),
                                                                                                                                                                 (7, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:33:20.919596', 'sukien@gmail.com', 'garden art', NULL, NULL),
                                                                                                                                                                 (8, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:36:12.975762', 'sukien@gmail.com', 'garden art', NULL, NULL),
                                                                                                                                                                 (9, '12032', 'VIET', 'sukien@gmail.com', '2026-04-17 06:41:18.171292', 'sukien@gmail.com', 'garden art', NULL, NULL),
                                                                                                                                                                 (10, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:46:25.015298', 'sukien@gmail.com', 'vivian', NULL, NULL),
                                                                                                                                                                 (11, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:49:16.771551', 'sukien@gmail.com', 'garden art', NULL, NULL),
                                                                                                                                                                 (12, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 06:52:12.020835', 'sukien@gmail.com', 'garden art', NULL, NULL),
                                                                                                                                                                 (13, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 07:00:17.268618', 'sukien@gmail.com', 'Aquafield Ocean City', NULL, NULL),
                                                                                                                                                                 (14, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 07:03:21.405813', 'sukien@gmail.com', 'KidZania', NULL, NULL),
                                                                                                                                                                 (15, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 07:07:00.429484', 'sukien@gmail.com', 'MeBayKuon', NULL, NULL),
                                                                                                                                                                 (16, '12032', 'VIET', 'sukien@gmmail.com', '2026-04-17 07:12:03.712411', 'sukien@gmail.com', 'MeBayLuon', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
                         `id` bigint NOT NULL,
                         `active` bit(1) NOT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `description` varchar(255) DEFAULT NULL,
                         `name` varchar(255) NOT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `active`, `created_at`, `created_by`, `description`, `name`, `updated_at`, `updated_by`) VALUES
                                                                                                                        (1, b'1', '2026-04-17 05:12:27.867042', '', 'Admin có full permissions', 'SUPER_ADMIN', NULL, NULL),
                                                                                                                        (2, b'1', '2026-04-17 05:12:27.924143', '', 'Customer chỉ được xem và cập nhật thông tin cá nhân, đặt vé.', 'CUSTOMER', NULL, NULL),
                                                                                                                        (3, b'1', '2026-04-17 05:12:27.961144', '', 'Organizer quản lý sự kiện, vé, ghế, nhân viên và thông tin cá nhân', 'ORGANIZER', NULL, NULL),
                                                                                                                        (4, b'1', '2026-04-17 05:12:27.986144', '', 'Staff có quyền quét QR vé sự kiện', 'STAFF', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `seats`
--

CREATE TABLE `seats` (
                         `id` bigint NOT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `price` double NOT NULL,
                         `seat_label` varchar(255) NOT NULL,
                         `status` enum('AVAILABLE','BOOKED','LOCKED') DEFAULT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `zone` varchar(255) NOT NULL,
                         `event_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `seats`
--

INSERT INTO `seats` (`id`, `created_at`, `created_by`, `price`, `seat_label`, `status`, `updated_at`, `updated_by`, `zone`, `event_id`) VALUES
                                                                                                                                            (1, '2026-04-17 06:02:58.456384', 'sukien@gmail.com', 500000, 'T1', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (2, '2026-04-17 06:02:58.456384', 'sukien@gmail.com', 500000, 'T3', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (3, '2026-04-17 06:02:58.456384', 'sukien@gmail.com', 500000, 'T6', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (4, '2026-04-17 06:02:58.456384', 'sukien@gmail.com', 500000, 'T5', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (5, '2026-04-17 06:02:58.456384', 'sukien@gmail.com', 500000, 'T2', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (6, '2026-04-17 06:02:58.456384', 'sukien@gmail.com', 500000, 'T4', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (7, '2026-04-17 06:02:58.493180', 'sukien@gmail.com', 500000, 'T11', 'BOOKED', '2026-04-20 04:00:02.831831', '1@gmail.com', 'Thuong', 3),
                                                                                                                                            (8, '2026-04-17 06:02:58.493180', 'sukien@gmail.com', 500000, 'T12', 'AVAILABLE', '2026-04-20 03:54:27.851818', '1@gmail.com', 'Thuong', 3),
                                                                                                                                            (9, '2026-04-17 06:02:58.494176', 'sukien@gmail.com', 500000, 'T7', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (10, '2026-04-17 06:02:58.494176', 'sukien@gmail.com', 500000, 'T10', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (11, '2026-04-17 06:02:58.494176', 'sukien@gmail.com', 500000, 'T8', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (12, '2026-04-17 06:02:58.495176', 'sukien@gmail.com', 500000, 'T9', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (13, '2026-04-17 06:02:58.512157', 'sukien@gmail.com', 500000, 'T14', 'AVAILABLE', '2026-04-20 03:59:38.232585', '1@gmail.com', 'Thuong', 3),
                                                                                                                                            (14, '2026-04-17 06:02:58.512157', 'sukien@gmail.com', 500000, 'T17', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (15, '2026-04-17 06:02:58.512157', 'sukien@gmail.com', 500000, 'T15', 'AVAILABLE', '2026-04-20 03:59:01.179975', '1@gmail.com', 'Thuong', 3),
                                                                                                                                            (16, '2026-04-17 06:02:58.512157', 'sukien@gmail.com', 500000, 'T13', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (17, '2026-04-17 06:02:58.520568', 'sukien@gmail.com', 500000, 'T16', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (18, '2026-04-17 06:02:58.520568', 'sukien@gmail.com', 500000, 'T18', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (19, '2026-04-17 06:02:58.546673', 'sukien@gmail.com', 1500000, 'V3', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (20, '2026-04-17 06:02:58.546673', 'sukien@gmail.com', 1500000, 'V1', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (21, '2026-04-17 06:02:58.546673', 'sukien@gmail.com', 500000, 'T20', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (22, '2026-04-17 06:02:58.546673', 'sukien@gmail.com', 1500000, 'V2', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (23, '2026-04-17 06:02:58.546673', 'sukien@gmail.com', 500000, 'T19', 'AVAILABLE', NULL, NULL, 'Thuong', 3),
                                                                                                                                            (24, '2026-04-17 06:02:58.546673', 'sukien@gmail.com', 1500000, 'V4', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (25, '2026-04-17 06:02:58.576555', 'sukien@gmail.com', 1500000, 'V7', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (26, '2026-04-17 06:02:58.576555', 'sukien@gmail.com', 1500000, 'V8', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (27, '2026-04-17 06:02:58.576555', 'sukien@gmail.com', 1500000, 'V6', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (28, '2026-04-17 06:02:58.576555', 'sukien@gmail.com', 1500000, 'V5', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (29, '2026-04-17 06:02:58.605598', 'sukien@gmail.com', 1500000, 'V9', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (30, '2026-04-17 06:02:58.612292', 'sukien@gmail.com', 1500000, 'V10', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (31, '2026-04-17 06:02:58.638749', 'sukien@gmail.com', 1500000, 'V11', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (32, '2026-04-17 06:02:58.638749', 'sukien@gmail.com', 1500000, 'V14', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (33, '2026-04-17 06:02:58.639751', 'sukien@gmail.com', 1500000, 'V12', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (34, '2026-04-17 06:02:58.639751', 'sukien@gmail.com', 1500000, 'V13', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (35, '2026-04-17 06:02:58.641749', 'sukien@gmail.com', 1500000, 'V15', 'AVAILABLE', '2026-04-20 04:00:32.722379', '1@gmail.com', 'Vip', 3),
                                                                                                                                            (36, '2026-04-17 06:02:58.643748', 'sukien@gmail.com', 1500000, 'V16', 'AVAILABLE', '2026-04-20 03:59:31.312729', '1@gmail.com', 'Vip', 3),
                                                                                                                                            (37, '2026-04-17 06:02:58.669319', 'sukien@gmail.com', 1500000, 'V18', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (38, '2026-04-17 06:02:58.669319', 'sukien@gmail.com', 1500000, 'V20', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (39, '2026-04-17 06:02:58.669319', 'sukien@gmail.com', 1500000, 'V17', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (40, '2026-04-17 06:02:58.775795', 'sukien@gmail.com', 1500000, 'V19', 'AVAILABLE', NULL, NULL, 'Vip', 3),
                                                                                                                                            (41, '2026-04-17 06:17:33.290849', 'sukien@gmail.com', 300000, 'T2', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (42, '2026-04-17 06:17:33.290849', 'sukien@gmail.com', 300000, 'T5', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (43, '2026-04-17 06:17:33.290849', 'sukien@gmail.com', 300000, 'T3', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (44, '2026-04-17 06:17:33.290849', 'sukien@gmail.com', 300000, 'T1', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (45, '2026-04-17 06:17:33.295638', 'sukien@gmail.com', 300000, 'T4', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (46, '2026-04-17 06:17:33.290849', 'sukien@gmail.com', 300000, 'T6', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (47, '2026-04-17 06:17:33.327809', 'sukien@gmail.com', 300000, 'T7', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (48, '2026-04-17 06:17:33.327809', 'sukien@gmail.com', 300000, 'T10', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (49, '2026-04-17 06:17:33.327809', 'sukien@gmail.com', 300000, 'T12', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (50, '2026-04-17 06:17:33.327809', 'sukien@gmail.com', 300000, 'T11', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (51, '2026-04-17 06:17:33.327809', 'sukien@gmail.com', 300000, 'T8', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (52, '2026-04-17 06:17:33.327809', 'sukien@gmail.com', 300000, 'T9', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (53, '2026-04-17 06:17:33.348657', 'sukien@gmail.com', 300000, 'T15', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (54, '2026-04-17 06:17:33.348657', 'sukien@gmail.com', 300000, 'T16', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (55, '2026-04-17 06:17:33.348657', 'sukien@gmail.com', 300000, 'T14', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (56, '2026-04-17 06:17:33.348657', 'sukien@gmail.com', 300000, 'T13', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (57, '2026-04-17 06:17:33.355729', 'sukien@gmail.com', 300000, 'T17', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (58, '2026-04-17 06:17:33.358740', 'sukien@gmail.com', 300000, 'T18', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (59, '2026-04-17 06:17:33.376452', 'sukien@gmail.com', 300000, 'T19', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (60, '2026-04-17 06:17:33.376452', 'sukien@gmail.com', 300000, 'T20', 'AVAILABLE', NULL, NULL, 'Thuong', 5),
                                                                                                                                            (61, '2026-04-17 06:17:33.376452', 'sukien@gmail.com', 500000, 'V1', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (62, '2026-04-17 06:17:33.376452', 'sukien@gmail.com', 500000, 'V3', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (63, '2026-04-17 06:17:33.385766', 'sukien@gmail.com', 500000, 'V2', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (64, '2026-04-17 06:17:33.385766', 'sukien@gmail.com', 500000, 'V4', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (65, '2026-04-17 06:17:33.408362', 'sukien@gmail.com', 500000, 'V5', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (66, '2026-04-17 06:17:33.410360', 'sukien@gmail.com', 500000, 'V6', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (67, '2026-04-17 06:17:33.409361', 'sukien@gmail.com', 500000, 'V7', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (68, '2026-04-17 06:17:33.415247', 'sukien@gmail.com', 500000, 'V8', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (69, '2026-04-17 06:17:33.415247', 'sukien@gmail.com', 500000, 'V10', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (70, '2026-04-17 06:17:33.415247', 'sukien@gmail.com', 500000, 'V9', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (71, '2026-04-17 06:17:33.436830', 'sukien@gmail.com', 500000, 'V11', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (72, '2026-04-17 06:17:33.439721', 'sukien@gmail.com', 500000, 'V12', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (73, '2026-04-17 06:17:33.441736', 'sukien@gmail.com', 500000, 'V13', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (74, '2026-04-17 06:17:33.444739', 'sukien@gmail.com', 500000, 'V14', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (75, '2026-04-17 06:17:33.445878', 'sukien@gmail.com', 500000, 'V15', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (76, '2026-04-17 06:17:33.445878', 'sukien@gmail.com', 500000, 'V16', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (77, '2026-04-17 06:17:33.459778', 'sukien@gmail.com', 500000, 'V17', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (78, '2026-04-17 06:17:33.459778', 'sukien@gmail.com', 500000, 'V18', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (79, '2026-04-17 06:17:33.459778', 'sukien@gmail.com', 500000, 'V19', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (80, '2026-04-17 06:17:33.468630', 'sukien@gmail.com', 500000, 'V20', 'AVAILABLE', NULL, NULL, 'Vip', 5),
                                                                                                                                            (81, '2026-04-17 06:28:55.162217', 'sukien@gmail.com', 300000, 'T1', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (82, '2026-04-17 06:28:55.162217', 'sukien@gmail.com', 300000, 'T5', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (83, '2026-04-17 06:28:55.162217', 'sukien@gmail.com', 300000, 'T2', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (84, '2026-04-17 06:28:55.162217', 'sukien@gmail.com', 300000, 'T3', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (85, '2026-04-17 06:28:55.165415', 'sukien@gmail.com', 300000, 'T4', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (86, '2026-04-17 06:28:55.173283', 'sukien@gmail.com', 300000, 'T6', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (87, '2026-04-17 06:28:55.193012', 'sukien@gmail.com', 300000, 'T11', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (88, '2026-04-17 06:28:55.207022', 'sukien@gmail.com', 300000, 'T8', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (89, '2026-04-17 06:28:55.207022', 'sukien@gmail.com', 300000, 'T12', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (90, '2026-04-17 06:28:55.208032', 'sukien@gmail.com', 300000, 'T9', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (91, '2026-04-17 06:28:55.207022', 'sukien@gmail.com', 300000, 'T10', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (92, '2026-04-17 06:28:55.210998', 'sukien@gmail.com', 300000, 'T7', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (93, '2026-04-17 06:28:55.232884', 'sukien@gmail.com', 300000, 'T13', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (94, '2026-04-17 06:28:55.232884', 'sukien@gmail.com', 300000, 'T14', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (95, '2026-04-17 06:28:55.236832', 'sukien@gmail.com', 300000, 'T15', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (96, '2026-04-17 06:28:55.240829', 'sukien@gmail.com', 300000, 'T18', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (97, '2026-04-17 06:28:55.240829', 'sukien@gmail.com', 300000, 'T17', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (98, '2026-04-17 06:28:55.241734', 'sukien@gmail.com', 300000, 'T16', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (99, '2026-04-17 06:28:55.259696', 'sukien@gmail.com', 300000, 'T19', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (100, '2026-04-17 06:28:55.260698', 'sukien@gmail.com', 300000, 'T20', 'AVAILABLE', NULL, NULL, 'Thuong', 6),
                                                                                                                                            (101, '2026-04-17 06:28:55.262699', 'sukien@gmail.com', 500000, 'V1', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (102, '2026-04-17 06:28:55.262699', 'sukien@gmail.com', 500000, 'V2', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (103, '2026-04-17 06:28:55.262699', 'sukien@gmail.com', 500000, 'V3', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (104, '2026-04-17 06:28:55.274222', 'sukien@gmail.com', 500000, 'V4', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (105, '2026-04-17 06:28:55.276227', 'sukien@gmail.com', 500000, 'V5', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (106, '2026-04-17 06:28:55.288927', 'sukien@gmail.com', 500000, 'V6', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (107, '2026-04-17 06:28:55.290422', 'sukien@gmail.com', 500000, 'V7', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (108, '2026-04-17 06:28:55.290422', 'sukien@gmail.com', 500000, 'V9', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (109, '2026-04-17 06:28:55.291434', 'sukien@gmail.com', 500000, 'V8', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (110, '2026-04-17 06:28:55.297219', 'sukien@gmail.com', 500000, 'V10', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (111, '2026-04-17 06:28:55.307359', 'sukien@gmail.com', 500000, 'V11', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (112, '2026-04-17 06:28:55.311145', 'sukien@gmail.com', 500000, 'V15', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (113, '2026-04-17 06:28:55.311145', 'sukien@gmail.com', 500000, 'V12', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (114, '2026-04-17 06:28:55.311145', 'sukien@gmail.com', 500000, 'V14', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (115, '2026-04-17 06:28:55.320492', 'sukien@gmail.com', 500000, 'V13', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (116, '2026-04-17 06:28:55.329230', 'sukien@gmail.com', 500000, 'V16', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (117, '2026-04-17 06:28:55.332231', 'sukien@gmail.com', 500000, 'V17', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (118, '2026-04-17 06:28:55.342017', 'sukien@gmail.com', 500000, 'V18', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (119, '2026-04-17 06:28:55.343017', 'sukien@gmail.com', 500000, 'V20', 'AVAILABLE', NULL, NULL, 'Vip', 6),
                                                                                                                                            (120, '2026-04-17 06:28:55.345022', 'sukien@gmail.com', 500000, 'V19', 'AVAILABLE', NULL, NULL, 'Vip', 6);

-- --------------------------------------------------------

--
-- Table structure for table `tickets`
--

CREATE TABLE `tickets` (
                           `id` bigint NOT NULL,
                           `created_at` datetime(6) DEFAULT NULL,
                           `created_by` varchar(255) DEFAULT NULL,
                           `price` double NOT NULL,
                           `sold_quantity` int NOT NULL,
                           `ticket_status` enum('PUBLISHED','SOLD_OUT','STOPPED') DEFAULT NULL,
                           `ticket_type` enum('STANDARD','VIP') DEFAULT NULL,
                           `total_quantity` int NOT NULL,
                           `updated_at` datetime(6) DEFAULT NULL,
                           `updated_by` varchar(255) DEFAULT NULL,
                           `event_id` bigint DEFAULT NULL
) ;

--
-- Dumping data for table `tickets`
--

INSERT INTO `tickets` (`id`, `created_at`, `created_by`, `price`, `sold_quantity`, `ticket_status`, `ticket_type`, `total_quantity`, `updated_at`, `updated_by`, `event_id`) VALUES
                                                                                                                                                                                 (1, '2026-04-17 05:50:01.376961', 'sukien@gmail.com', 900000, 0, 'PUBLISHED', 'STANDARD', 30, NULL, NULL, 1),
                                                                                                                                                                                 (2, '2026-04-17 05:50:01.376961', 'sukien@gmail.com', 1500000, 0, 'PUBLISHED', 'VIP', 30, NULL, NULL, 1),
                                                                                                                                                                                 (3, '2026-04-17 05:56:18.596987', 'sukien@gmail.com', 789000, 0, 'PUBLISHED', 'STANDARD', 30, NULL, NULL, 2),
                                                                                                                                                                                 (4, '2026-04-17 05:56:18.596987', 'sukien@gmail.com', 1500000, 0, 'PUBLISHED', 'STANDARD', 30, NULL, NULL, 2),
                                                                                                                                                                                 (5, '2026-04-17 06:02:58.345256', 'sukien@gmail.com', 500000, 2, 'PUBLISHED', 'STANDARD', 20, '2026-04-20 04:00:02.831831', '1@gmail.com', 3),
                                                                                                                                                                                 (6, '2026-04-17 06:02:58.357070', 'sukien@gmail.com', 1500000, 0, 'PUBLISHED', 'STANDARD', 20, NULL, NULL, 3),
                                                                                                                                                                                 (7, '2026-04-17 06:08:42.802238', 'sukien@gmail.com', 1200000, 0, 'PUBLISHED', 'STANDARD', 20, NULL, NULL, 4),
                                                                                                                                                                                 (8, '2026-04-17 06:08:42.802238', 'sukien@gmail.com', 2000000, 0, 'PUBLISHED', 'VIP', 20, NULL, NULL, 4),
                                                                                                                                                                                 (9, '2026-04-17 06:17:33.230455', 'sukien@gmail.com', 500000, 0, 'PUBLISHED', 'VIP', 20, NULL, NULL, 5),
                                                                                                                                                                                 (10, '2026-04-17 06:17:33.230455', 'sukien@gmail.com', 300000, 0, 'PUBLISHED', 'STANDARD', 20, NULL, NULL, 5),
                                                                                                                                                                                 (11, '2026-04-17 06:28:55.075413', 'sukien@gmail.com', 300000, 0, 'PUBLISHED', 'STANDARD', 20, NULL, NULL, 6),
                                                                                                                                                                                 (12, '2026-04-17 06:28:55.075413', 'sukien@gmail.com', 500000, 0, 'PUBLISHED', 'VIP', 20, NULL, NULL, 6),
                                                                                                                                                                                 (13, '2026-04-17 06:33:20.950113', 'sukien@gmail.com', 390000, 0, 'PUBLISHED', 'STANDARD', 50, NULL, NULL, 7),
                                                                                                                                                                                 (14, '2026-04-17 06:36:13.003558', 'sukien@gmail.com', 420000, 0, 'PUBLISHED', 'STANDARD', 50, NULL, NULL, 8),
                                                                                                                                                                                 (15, '2026-04-17 06:41:18.184089', 'sukien@gmail.com', 50000, 0, 'PUBLISHED', 'STANDARD', 30, NULL, NULL, 9),
                                                                                                                                                                                 (16, '2026-04-17 06:46:25.029187', 'sukien@gmail.com', 315000, 0, 'PUBLISHED', 'STANDARD', 40, NULL, NULL, 10),
                                                                                                                                                                                 (17, '2026-04-17 06:49:16.795650', 'sukien@gmail.com', 390000, 0, 'PUBLISHED', 'STANDARD', 30, NULL, NULL, 11),
                                                                                                                                                                                 (18, '2026-04-17 06:52:12.150385', 'sukien@gmail.com', 370000, 0, 'PUBLISHED', 'STANDARD', 30, NULL, NULL, 12),
                                                                                                                                                                                 (19, '2026-04-17 07:00:17.303465', 'sukien@gmail.com', 170000, 0, 'PUBLISHED', 'STANDARD', 100, NULL, NULL, 13),
                                                                                                                                                                                 (20, '2026-04-17 07:03:21.430775', 'sukien@gmail.com', 50000, 0, 'PUBLISHED', 'STANDARD', 100, NULL, NULL, 14),
                                                                                                                                                                                 (21, '2026-04-17 07:07:00.458435', 'sukien@gmail.com', 1850000, 0, 'PUBLISHED', 'STANDARD', 100, NULL, NULL, 15),
                                                                                                                                                                                 (22, '2026-04-17 07:12:03.751971', 'sukien@gmail.com', 2190000, 0, 'PUBLISHED', 'STANDARD', 100, NULL, NULL, 16);

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
                                `id` bigint NOT NULL,
                                `amount` double NOT NULL,
                                `created_at` datetime(6) DEFAULT NULL,
                                `created_by` varchar(255) DEFAULT NULL,
                                `payment_method` varchar(255) DEFAULT NULL,
                                `status` enum('FAILED','REFUNDED','SUCCESS') DEFAULT NULL,
                                `updated_at` datetime(6) DEFAULT NULL,
                                `updated_by` varchar(255) DEFAULT NULL,
                                `user_ticket_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`id`, `amount`, `created_at`, `created_by`, `payment_method`, `status`, `updated_at`, `updated_by`, `user_ticket_id`) VALUES
    (2, 500000, '2026-04-20 04:00:02.888624', '1@gmail.com', 'ONLINE', 'SUCCESS', NULL, NULL, 2);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `id` bigint NOT NULL,
                         `address` varchar(255) DEFAULT NULL,
                         `age` int NOT NULL,
                         `avatar` varchar(255) DEFAULT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `email` varchar(255) NOT NULL,
                         `gender` tinyint DEFAULT NULL,
                         `name` varchar(255) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `refresh_token` mediumtext,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `role_id` bigint DEFAULT NULL
) ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `address`, `age`, `avatar`, `created_at`, `created_by`, `email`, `gender`, `name`, `password`, `refresh_token`, `updated_at`, `updated_by`, `role_id`) VALUES
                                                                                                                                                                                      (1, 'hcm', 25, NULL, '2026-04-17 05:12:28.142353', '', 'admin@gmail.com', 0, 'I\'m super admin', '$2a$10$uxSpdbzFTfm7py5fgcKLcuWIFBkDntDcSVb84G0uMt8kYnbUMv7WS', NULL, '2026-04-20 04:46:34.835321', 'admin@gmail.com', 1),
                                                                                                                                                                                      (2, NULL, 0, NULL, '2026-04-17 05:24:51.715836', 'anonymousUser', 'sukien@gmail.com', NULL, 'sukien', '$2a$10$8TRoclpx/CoWA57OK9qFmeTErimGFOSaDxUlC9XeQNN2v7rALXWcm', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdWtpZW5AZ21haWwuY29tIiwiZXhwIjoxNzc5MjUyNDA1LCJpYXQiOjE3NzY2NjA0MDUsInVzZXIiOnsiaWQiOjIsImVtYWlsIjoic3VraWVuQGdtYWlsLmNvbSIsIm5hbWUiOiJzdWtpZW4iLCJhdmF0YXIiOm51bGx9fQ.qmMxV2iXrKY07qFYnHMnE-GtdcCKAaQjLdwgu7pNM0YGBSdldeacgz8_Z9TRUm7rzg8g_i64HgnrDhofVktPnA', '2026-04-20 04:46:45.595766', 'sukien@gmail.com', 3),
                                                                                                                                                                                      (3, NULL, 0, NULL, '2026-04-19 17:50:39.650946', 'anonymousUser', '1@gmail.com', NULL, '1', '$2a$10$cTz.bq3zB2DDYxTY15Q4Z.th90fNdc7DZ3GP9HV8zDM5w3TyzE9g2', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxQGdtYWlsLmNvbSIsImV4cCI6MTc3OTI1MzM5NCwiaWF0IjoxNzc2NjYxMzk0LCJ1c2VyIjp7ImlkIjozLCJlbWFpbCI6IjFAZ21haWwuY29tIiwibmFtZSI6IjEiLCJhdmF0YXIiOm51bGx9fQ.uCybYwnomp1mN0HH-U9XIEUQEH0eHUOG2U5VaLHPfpzjqsRHNkN7KCweyGfPj_YDy2geFEMJq9TjcOV4uP1ZUQ', '2026-04-20 05:03:14.461102', '1@gmail.com', 4);

-- --------------------------------------------------------

--
-- Table structure for table `user_favorite_artists`
--

CREATE TABLE `user_favorite_artists` (
                                         `user_id` bigint NOT NULL,
                                         `artist_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_tickets`
--

CREATE TABLE `user_tickets` (
                                `id` bigint NOT NULL,
                                `issued_at` datetime(6) DEFAULT NULL,
                                `qr_code` varchar(255) NOT NULL,
                                `status` enum('CANCELLED','EXPIRED','USED','VALID') DEFAULT NULL,
                                `used_at` datetime(6) DEFAULT NULL,
                                `event_id` bigint DEFAULT NULL,
                                `order_id` bigint DEFAULT NULL,
                                `seat_id` bigint DEFAULT NULL,
                                `ticket_id` bigint DEFAULT NULL,
                                `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user_tickets`
--

INSERT INTO `user_tickets` (`id`, `issued_at`, `qr_code`, `status`, `used_at`, `event_id`, `order_id`, `seat_id`, `ticket_id`, `user_id`) VALUES
    (2, '2026-04-20 04:00:02.824829', 'ab60d698-4058-4352-8923-2150b12c2604', 'VALID', NULL, 3, 21, 7, 5, 3);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat_message`
--
ALTER TABLE `chat_message`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `documents`
--
ALTER TABLE `documents`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `document_chunks`
--
ALTER TABLE `document_chunks`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKks8knsiau23lcmv9mydqjmj84` (`document_id`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK2dgitr96hv84be389ofxq53sc` (`genre_id`),
    ADD KEY `FK6wktmk2jidl1afmd136vp13wg` (`producer_id`);

--
-- Indexes for table `event_artists`
--
ALTER TABLE `event_artists`
    ADD KEY `FKc2j8e246t81k33lciy0bb0o08` (`event_id`);

--
-- Indexes for table `event_image`
--
ALTER TABLE `event_image`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKjg575nv0idnlj8jg3j9sm6ndv` (`event_id`);

--
-- Indexes for table `event_staffs`
--
ALTER TABLE `event_staffs`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKrgamvy5orxlkt1do5silsuouh` (`event_id`),
    ADD KEY `FKluxxj9wvsx9btfo7jyj6njfod` (`user_id`);

--
-- Indexes for table `genres`
--
ALTER TABLE `genres`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
    ADD KEY `FKg11tdqj0ekpb66qi8l6kfeq7r` (`seat_id`),
    ADD KEY `FKt2ynanr8tfohjdpkrjiajbmc` (`ticket_id`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `permission_role`
--
ALTER TABLE `permission_role`
    ADD KEY `FK6mg4g9rc8u87l0yavf8kjut05` (`permission_id`),
    ADD KEY `FK3vhflqw0lwbwn49xqoivrtugt` (`role_id`);

--
-- Indexes for table `producers`
--
ALTER TABLE `producers`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `seats`
--
ALTER TABLE `seats`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKn8dwqflg9k82ygrbsseghd7ca` (`event_id`);

--
-- Indexes for table `tickets`
--
ALTER TABLE `tickets`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK3utafe14rupaypjocldjaj4ol` (`event_id`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK4h2kqw97t38bd8vpkq61wakmi` (`user_ticket_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`);

--
-- Indexes for table `user_favorite_artists`
--
ALTER TABLE `user_favorite_artists`
    ADD KEY `FKk2jwqoplfu22jmb22o1as5v8i` (`user_id`);

--
-- Indexes for table `user_tickets`
--
ALTER TABLE `user_tickets`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `UK6127xch80f0ns37rrreirs3kq` (`qr_code`),
    ADD KEY `FKookaobtkmay8mos59byqavyya` (`event_id`),
    ADD KEY `FK1401i0q1rym1s2d6myxe71yin` (`order_id`),
    ADD KEY `FKetai6pwbyg59bs6tma8xg28al` (`seat_id`),
    ADD KEY `FK5m02ekua7b8pdma3kaft3ujll` (`ticket_id`),
    ADD KEY `FK9ok1dlkt49it7wyeh529wy315` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chat_message`
--
ALTER TABLE `chat_message`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `document_chunks`
--
ALTER TABLE `document_chunks`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `event_image`
--
ALTER TABLE `event_image`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `event_staffs`
--
ALTER TABLE `event_staffs`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `genres`
--
ALTER TABLE `genres`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=86;

--
-- AUTO_INCREMENT for table `producers`
--
ALTER TABLE `producers`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `seats`
--
ALTER TABLE `seats`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=121;

--
-- AUTO_INCREMENT for table `tickets`
--
ALTER TABLE `tickets`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_tickets`
--
ALTER TABLE `user_tickets`
    MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `document_chunks`
--
ALTER TABLE `document_chunks`
    ADD CONSTRAINT `FKks8knsiau23lcmv9mydqjmj84` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`);

--
-- Constraints for table `events`
--
ALTER TABLE `events`
    ADD CONSTRAINT `FK2dgitr96hv84be389ofxq53sc` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`),
    ADD CONSTRAINT `FK6wktmk2jidl1afmd136vp13wg` FOREIGN KEY (`producer_id`) REFERENCES `producers` (`id`);

--
-- Constraints for table `event_artists`
--
ALTER TABLE `event_artists`
    ADD CONSTRAINT `FKc2j8e246t81k33lciy0bb0o08` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);

--
-- Constraints for table `event_image`
--
ALTER TABLE `event_image`
    ADD CONSTRAINT `FKjg575nv0idnlj8jg3j9sm6ndv` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);

--
-- Constraints for table `event_staffs`
--
ALTER TABLE `event_staffs`
    ADD CONSTRAINT `FKluxxj9wvsx9btfo7jyj6njfod` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    ADD CONSTRAINT `FKrgamvy5orxlkt1do5silsuouh` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
    ADD CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
    ADD CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    ADD CONSTRAINT `FKg11tdqj0ekpb66qi8l6kfeq7r` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`),
    ADD CONSTRAINT `FKt2ynanr8tfohjdpkrjiajbmc` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`);

--
-- Constraints for table `permission_role`
--
ALTER TABLE `permission_role`
    ADD CONSTRAINT `FK3vhflqw0lwbwn49xqoivrtugt` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
    ADD CONSTRAINT `FK6mg4g9rc8u87l0yavf8kjut05` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`);

--
-- Constraints for table `seats`
--
ALTER TABLE `seats`
    ADD CONSTRAINT `FKn8dwqflg9k82ygrbsseghd7ca` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);

--
-- Constraints for table `tickets`
--
ALTER TABLE `tickets`
    ADD CONSTRAINT `FK3utafe14rupaypjocldjaj4ol` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
    ADD CONSTRAINT `FK4h2kqw97t38bd8vpkq61wakmi` FOREIGN KEY (`user_ticket_id`) REFERENCES `user_tickets` (`id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
    ADD CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);

--
-- Constraints for table `user_favorite_artists`
--
ALTER TABLE `user_favorite_artists`
    ADD CONSTRAINT `FKk2jwqoplfu22jmb22o1as5v8i` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `user_tickets`
--
ALTER TABLE `user_tickets`
    ADD CONSTRAINT `FK1401i0q1rym1s2d6myxe71yin` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    ADD CONSTRAINT `FK5m02ekua7b8pdma3kaft3ujll` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`),
    ADD CONSTRAINT `FK9ok1dlkt49it7wyeh529wy315` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    ADD CONSTRAINT `FKetai6pwbyg59bs6tma8xg28al` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`),
    ADD CONSTRAINT `FKookaobtkmay8mos59byqavyya` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;