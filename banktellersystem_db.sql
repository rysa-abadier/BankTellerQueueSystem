-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Jul 28, 2025 at 05:57 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `banktellersystem_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `teller_id` int(11) DEFAULT NULL,
  `service_id` int(11) NOT NULL,
  `queue_no` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('Queued','Active','Completed','Reassigned','Skipped') NOT NULL DEFAULT 'Queued',
  `emergency` enum('Yes','No') NOT NULL DEFAULT 'No',
  `transaction_date` datetime NOT NULL DEFAULT current_timestamp(),
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `teller_id`, `service_id`, `queue_no`, `name`, `status`, `emergency`, `transaction_date`, `start_time`, `end_time`) VALUES
(1, 1, 1, 1, 'Iris Ramos', 'Completed', 'No', '2025-07-21 08:10:00', '08:15:00', '08:20:00'),
(2, 2, 5, 2, 'Ken Soriano', 'Completed', 'No', '2025-07-21 08:25:00', '08:27:00', '08:31:00'),
(3, 3, 2, 3, 'Paula Ferrer', 'Completed', 'Yes', '2025-07-21 08:50:00', '08:55:00', '09:00:00'),
(4, 4, 3, 4, 'Leo Sison', 'Completed', 'No', '2025-07-21 09:10:00', '09:15:00', '09:30:00'),
(5, 5, 4, 5, 'Anna Pascual', 'Completed', 'No', '2025-07-21 09:30:00', '09:40:00', '10:00:00'),
(6, 1, 1, 6, 'Rico Dela Cruz', 'Completed', 'No', '2025-07-21 10:00:00', '10:03:00', '10:08:00'),
(7, 2, 5, 1, 'Maribel Santos', 'Completed', 'No', '2025-07-22 08:00:00', '08:03:00', '08:07:00'),
(8, 3, 2, 2, 'Joseph Lee', 'Completed', 'Yes', '2025-07-22 08:15:00', '08:20:00', '08:27:00'),
(9, 4, 3, 3, 'Shiela Tan', 'Completed', 'No', '2025-07-22 08:30:00', '08:36:00', '08:51:00'),
(10, 5, 4, 4, 'Danilo Javier', 'Completed', 'No', '2025-07-22 09:00:00', '09:08:00', '09:35:00'),
(11, 1, 1, 5, 'Lara Domingo', 'Completed', 'No', '2025-07-22 09:40:00', '09:46:00', '09:51:00'),
(12, 2, 5, 6, 'Vincent Go', 'Completed', 'No', '2025-07-22 10:00:00', '10:04:00', '10:08:00'),
(13, 3, 2, 1, 'April Alcantara', 'Completed', 'Yes', '2025-07-23 08:00:00', '08:04:00', '08:11:00'),
(14, 4, 3, 2, 'Fred Agustin', 'Completed', 'No', '2025-07-23 08:20:00', '08:23:00', '08:40:00'),
(15, 5, 4, 3, 'Monique Razon', 'Completed', 'No', '2025-07-23 08:45:00', '08:50:00', '09:15:00'),
(16, 1, 1, 4, 'Jorge Pineda', 'Completed', 'No', '2025-07-23 09:00:00', '09:05:00', '09:10:00'),
(17, 2, 5, 5, 'Nina Bernardo', 'Completed', 'No', '2025-07-23 09:20:00', '09:22:00', '09:26:00'),
(18, 3, 2, 6, 'Erwin Tolentino', 'Completed', 'No', '2025-07-23 09:45:00', '09:50:00', '09:57:00'),
(19, 4, 3, 1, 'Karina Lopez', 'Completed', 'No', '2025-07-24 08:05:00', '08:10:00', '08:25:00'),
(20, 5, 4, 2, 'Martin Cruz', 'Completed', 'Yes', '2025-07-24 08:30:00', '08:35:00', '08:58:00'),
(21, 1, 1, 3, 'Bea Jimenez', 'Completed', 'No', '2025-07-24 09:00:00', '09:06:00', '09:11:00'),
(22, 2, 5, 4, 'Xander Beltran', 'Completed', 'No', '2025-07-24 09:20:00', '09:23:00', '09:28:00'),
(23, 3, 2, 5, 'Diana Arellano', 'Completed', 'No', '2025-07-24 09:45:00', '09:50:00', '09:58:00'),
(24, 4, 3, 6, 'Rafael Enriquez', 'Completed', 'Yes', '2025-07-24 10:10:00', '10:15:00', '10:35:00'),
(25, 5, 4, 1, 'Celine Bautista', 'Completed', 'No', '2025-07-25 08:00:00', '08:02:00', '08:27:00'),
(26, 1, 1, 2, 'Justin Mercado', 'Completed', 'No', '2025-07-25 08:30:00', '08:36:00', '08:42:00'),
(27, 2, 5, 3, 'Sharmaine Reyes', 'Completed', 'No', '2025-07-25 09:00:00', '09:03:00', '09:06:00'),
(28, 3, 2, 4, 'Rico Velasquez', 'Completed', 'No', '2025-07-25 09:30:00', '09:36:00', '09:44:00'),
(29, 4, 3, 5, 'Nora Samonte', 'Completed', 'Yes', '2025-07-25 10:00:00', '10:07:00', '10:23:00'),
(30, 5, 4, 6, 'Tony Aquino', 'Completed', 'No', '2025-07-25 10:30:00', '10:34:00', '11:00:00'),
(31, 1, 1, 1, 'Camille Tan', 'Completed', 'No', '2025-07-26 08:20:00', '08:23:00', '08:28:00'),
(32, 2, 5, 2, 'Jerome Delos Santos', 'Completed', 'No', '2025-07-26 08:40:00', '08:42:00', '08:47:00'),
(33, 3, 2, 3, 'Yassi Fernandez', 'Completed', 'No', '2025-07-26 09:05:00', '09:10:00', '09:15:00'),
(34, 4, 3, 4, 'Luis Cabrera', 'Completed', 'Yes', '2025-07-26 09:30:00', '09:36:00', '09:55:00'),
(35, 5, 4, 5, 'Gwen Agustin', 'Completed', 'No', '2025-07-26 10:00:00', '10:07:00', '10:33:00'),
(36, 1, 1, 6, 'Bong De Vera', 'Completed', 'No', '2025-07-26 10:30:00', '10:35:00', '10:42:00'),
(37, 2, 5, 1, 'Rina Gallardo', 'Completed', 'No', '2025-07-27 08:15:00', '08:18:00', '08:23:00'),
(38, 3, 2, 2, 'Gio Angeles', 'Completed', 'Yes', '2025-07-27 08:35:00', '08:40:00', '08:45:00'),
(39, 4, 3, 3, 'Sofia Garcia', 'Completed', 'No', '2025-07-27 09:00:00', '09:06:00', '09:25:00'),
(40, 5, 4, 4, 'Eman Mendez', 'Completed', 'No', '2025-07-27 09:30:00', '09:36:00', '09:55:00'),
(41, 1, 1, 5, 'Trisha Espejo', 'Completed', 'No', '2025-07-27 10:00:00', '10:06:00', '10:13:00'),
(42, 2, 5, 6, 'Zyrene Robles', 'Completed', 'Yes', '2025-07-27 10:20:00', '10:25:00', '10:30:00'),
(43, 1, 1, 2, 'Harold Uy', 'Active', 'Yes', '2025-07-28 08:10:00', '08:14:00', NULL),
(44, 2, 5, 3, 'Charlene Villanueva', 'Queued', 'No', '2025-07-28 08:20:00', NULL, NULL),
(45, 3, 2, 4, 'Jayson Mendoza', 'Queued', 'No', '2025-07-28 08:25:00', NULL, NULL),
(46, 4, 3, 5, 'Grace Yabut', 'Reassigned', 'Yes', '2025-07-28 08:30:00', NULL, NULL),
(47, 5, 4, 6, 'Ivan Macapagal', 'Queued', 'No', '2025-07-28 08:40:00', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

CREATE TABLE `services` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `priority` enum('Critical','High','Medium','Low') DEFAULT NULL,
  `average_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `services`
--

INSERT INTO `services` (`id`, `name`, `priority`, `average_time`) VALUES
(1, 'Deposit', 'Medium', '00:05:00'),
(2, 'Withdrawal', 'Medium', '00:05:00'),
(3, 'Loan Inquiry', 'High', '00:15:00'),
(4, 'Account Opening', 'Critical', '00:25:00'),
(5, 'Bills Payment', 'Low', '00:04:00');

-- --------------------------------------------------------

--
-- Table structure for table `tellers`
--

CREATE TABLE `tellers` (
  `id` int(11) NOT NULL,
  `service_id` int(11) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tellers`
--

INSERT INTO `tellers` (`id`, `service_id`, `first_name`, `last_name`) VALUES
(1, 1, 'Rysa', 'Abadier'),
(2, 5, 'Evana Isabella', 'Bronilla'),
(3, 2, 'Mariah Judea', 'Julian'),
(4, 3, 'Isabella Mikaella', 'Llave'),
(5, 4, 'Noah', 'Peñaranda');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tellers`
--
ALTER TABLE `tellers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `service_id` (`service_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `services`
--
ALTER TABLE `services`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tellers`
--
ALTER TABLE `tellers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tellers`
--
ALTER TABLE `tellers`
  ADD CONSTRAINT `tellers_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
