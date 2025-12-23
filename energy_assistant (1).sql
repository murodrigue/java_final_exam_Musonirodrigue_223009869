-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 23, 2025 at 07:35 AM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `energy_assistant`
--

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
CREATE TABLE IF NOT EXISTS `customer` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `PasswordHash` varchar(255) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `FullName` varchar(100) NOT NULL,
  `Role` enum('admin','user') DEFAULT 'user',
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastLogin` timestamp NULL DEFAULT NULL,
  `IsActive` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`CustomerID`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`CustomerID`, `Username`, `PasswordHash`, `Email`, `FullName`, `Role`, `CreatedAt`, `LastLogin`, `IsActive`) VALUES
(2, 'Rodrigue', '123456', 'musonirodriguez@gmail.com', 'Rodrigue Musoni', 'user', '2025-11-23 15:10:12', '2025-12-22 18:39:12', 1),
(3, 'japhet', '234567', 'japhet@gmail.com', 'japhet', 'user', '2025-11-23 16:23:07', '2025-11-23 16:29:29', 1),
(4, 'ange', '345678', 'ange@gmail.com', 'ange', 'user', '2025-11-24 07:32:45', '2025-12-19 15:41:16', 1),
(5, 'vida', 'vida1234', 'vida@gmail.com', 'vida sol', 'user', '2025-12-13 18:30:50', NULL, 1),
(6, 'ANETHY', '123456', 'anethy@gmail.com', 'anethy uwizeye', 'user', '2025-12-21 10:27:45', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `maintenance`
--

DROP TABLE IF EXISTS `maintenance`;
CREATE TABLE IF NOT EXISTS `maintenance` (
  `MaintenanceID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `ReferenceID` varchar(100) DEFAULT NULL,
  `Description` text,
  `ScheduleDate` date DEFAULT NULL,
  `CompletionDate` date DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'Scheduled',
  `Remarks` text,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MaintenanceID`),
  KEY `CustomerID` (`CustomerID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `maintenance`
--

INSERT INTO `maintenance` (`MaintenanceID`, `CustomerID`, `ReferenceID`, `Description`, `ScheduleDate`, `CompletionDate`, `Status`, `Remarks`, `CreatedAt`) VALUES
(1, 1, 'MTN-2025-001', 'Annual inspection of main electrical panel', '2025-11-10', '2025-11-10', 'Completed', 'All components in good condition', '2025-12-13 19:39:06');

-- --------------------------------------------------------

--
-- Table structure for table `meter`
--

DROP TABLE IF EXISTS `meter`;
CREATE TABLE IF NOT EXISTS `meter` (
  `MeterID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `Name` varchar(100) NOT NULL,
  `Description` text,
  `Category` varchar(50) DEFAULT NULL,
  `PriceOrValue` decimal(10,2) DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'Active',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MeterID`),
  KEY `CustomerID` (`CustomerID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `meter`
--

INSERT INTO `meter` (`MeterID`, `CustomerID`, `Name`, `Description`, `Category`, `PriceOrValue`, `Status`, `CreatedAt`) VALUES
(1, 1, 'Main Building Meter', 'Primary electricity meter for main building', 'Electricity', 1500.00, 'Active', '2025-11-23 10:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `outage`
--

DROP TABLE IF EXISTS `outage`;
CREATE TABLE IF NOT EXISTS `outage` (
  `OutageID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `OutageType` varchar(50) DEFAULT NULL,
  `StartTime` datetime DEFAULT NULL,
  `EndTime` datetime DEFAULT NULL,
  `Area` varchar(100) DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'Reported',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OutageID`),
  KEY `CustomerID` (`CustomerID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `outage`
--

INSERT INTO `outage` (`OutageID`, `CustomerID`, `OutageType`, `StartTime`, `EndTime`, `Area`, `Status`, `CreatedAt`) VALUES
(1, 1, 'Planned', '2025-11-15 09:00:00', '2025-11-15 12:00:00', 'Downtown District', 'Resolved', '2025-12-13 19:36:45');

-- --------------------------------------------------------

--
-- Table structure for table `plan`
--

DROP TABLE IF EXISTS `plan`;
CREATE TABLE IF NOT EXISTS `plan` (
  `PlanID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `PlanName` varchar(100) NOT NULL,
  `Description` text,
  `TariffRate` decimal(8,4) DEFAULT NULL,
  `MonthlyFee` decimal(8,2) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PlanID`),
  KEY `CustomerID` (`CustomerID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `plan`
--

INSERT INTO `plan` (`PlanID`, `CustomerID`, `PlanName`, `Description`, `TariffRate`, `MonthlyFee`, `CreatedAt`) VALUES
(1, 1, 'Commercial Premium', 'Premium plan for commercial buildings with high consumption', 0.1850, 150.00, '2025-12-13 19:34:32');

-- --------------------------------------------------------

--
-- Table structure for table `planoutage`
--

DROP TABLE IF EXISTS `planoutage`;
CREATE TABLE IF NOT EXISTS `planoutage` (
  `PlanID` int NOT NULL,
  `OutageID` int NOT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PlanID`,`OutageID`),
  KEY `OutageID` (`OutageID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reading`
--

DROP TABLE IF EXISTS `reading`;
CREATE TABLE IF NOT EXISTS `reading` (
  `ReadingID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `MeterID` int DEFAULT NULL,
  `Amount` decimal(10,2) NOT NULL,
  `Date` date NOT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `Reference` varchar(100) DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'Recorded',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ReadingID`),
  KEY `CustomerID` (`CustomerID`),
  KEY `MeterID` (`MeterID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reading`
--

INSERT INTO `reading` (`ReadingID`, `CustomerID`, `MeterID`, `Amount`, `Date`, `Type`, `Reference`, `Status`, `CreatedAt`) VALUES
(1, 1, 1, 1250.75, '2025-11-01', 'Electricity', 'INV-2025-001', 'Paid', '2025-12-13 19:30:57');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
