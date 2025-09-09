-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 08, 2025 at 01:30 PM
-- Server version: 11.4.4-MariaDB-log
-- PHP Version: 8.3.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `test_chando`
--

-- --------------------------------------------------------

--
-- Table structure for table `Person`
--

CREATE TABLE `Person` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `reg_id` varchar(50) NOT NULL,
  `Type` enum('CAR','BIKE','TRUCK') NOT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Person`
--
 

-- --------------------------------------------------------

--
-- Table structure for table `Pricing`
--

CREATE TABLE `Pricing` (
  `id` int(11) NOT NULL,
  `spot_type` varchar(20) NOT NULL,
  `fees_per_hour` decimal(10,2) NOT NULL,
  `penalty_per_hour` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Pricing`
-- 

-- --------------------------------------------------------

--
-- Table structure for table `Spot`
--

CREATE TABLE `Spot` (
  `ID` int(11) NOT NULL,
  `Lat` decimal(10,6) NOT NULL,
  `Lon` decimal(10,6) NOT NULL,
  `spot_type` varchar(20) NOT NULL,
  `status` enum('AVAILABLE','RESERVED','OCCUPIED') DEFAULT 'AVAILABLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Spot`
-- 
-- --------------------------------------------------------

--
-- Table structure for table `Token`
--

CREATE TABLE `Token` (
  `token_id` int(11) NOT NULL,
  `reg_id` varchar(20) NOT NULL,
  `spot_id` int(11) NOT NULL,
  `amount` decimal(10,2) DEFAULT 0.00,
  `penalty_amount` decimal(10,2) DEFAULT 0.00,
  `arrival_time` datetime NOT NULL,
  `leaved_at` datetime DEFAULT NULL,
  `status` enum('ACTIVE','EXPIRED') DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Token`
-- 
-- --------------------------------------------------------

--
-- Table structure for table `TokenLog`
--

CREATE TABLE `TokenLog` (
  `log_id` int(11) NOT NULL,
  `token_id` int(11) NOT NULL,
  `status` enum('BOOKED','DEPARTED','PENALIZED') DEFAULT NULL,
  `time` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `TokenLog`
-- 
--
-- Indexes for dumped tables
--

--
-- Indexes for table `Person`
--
ALTER TABLE `Person`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `Pricing`
--
ALTER TABLE `Pricing`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `spot_type` (`spot_type`);

--
-- Indexes for table `Spot`
--
ALTER TABLE `Spot`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `spot_type` (`spot_type`);

--
-- Indexes for table `Token`
--
ALTER TABLE `Token`
  ADD PRIMARY KEY (`token_id`),
  ADD KEY `reg_id` (`reg_id`),
  ADD KEY `slot_id` (`spot_id`);

--
-- Indexes for table `TokenLog`
--
ALTER TABLE `TokenLog`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `token_id` (`token_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Person`
--
ALTER TABLE `Person`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `Pricing`
--
ALTER TABLE `Pricing`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `Spot`
--
ALTER TABLE `Spot`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=190;

--
-- AUTO_INCREMENT for table `Token`
--
ALTER TABLE `Token`
  MODIFY `token_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `TokenLog`
--
ALTER TABLE `TokenLog`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Spot`
--
ALTER TABLE `Spot`
  ADD CONSTRAINT `Spot_ibfk_1` FOREIGN KEY (`spot_type`) REFERENCES `Pricing` (`spot_type`) ON DELETE CASCADE;

--
-- Constraints for table `TokenLog`
--
ALTER TABLE `TokenLog`
  ADD CONSTRAINT `TokenLog_ibfk_1` FOREIGN KEY (`token_id`) REFERENCES `Token` (`token_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
