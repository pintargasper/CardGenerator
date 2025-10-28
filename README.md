<div align="center">

<img src="social-preview.png" width="200px" alt="Social Preview">
<br>
<h1>Card Generator</h1>

![OS](https://img.shields.io/badge/Windows-lightgrey)
![OS](https://img.shields.io/badge/Linux-lightgrey)
[![Downloads](https://img.shields.io/github/downloads/pintargasper/cardgenerator/total?style=flat-square)](https://github.com/pintargasper/CardGenerator/releases)

</div>

---
<div align="center">

[![GitHub Releases](https://custom-icon-badges.herokuapp.com/badge/Website-lightgray?style=for-the-badge&logo=website&logoColor=white)](https://gasperpintar.com/card-generator)

[![GitHub Releases](https://custom-icon-badges.herokuapp.com/badge/Download-lightgray?style=for-the-badge&logo=download&logoColor=white)](https://github.com/pintargasper/CardGenerator/releases/latest)

</div>

## Table of Contents
- [About](#-about)
- [Supported Languages](#-supported-languages)
- [Dependencies & Versions](#-dependencies--versions)
- [How to Build](#-how-to-build)

## 🚀 About

**Card Generator** is a Windows and Linux application written in **Java**, designed to help users easily create and manage custom cards. 
The app allows users to **display generated cards** and **export them** in multiple formats

**Key Features**
- **Display generated cards** directly in the application
- **Export cards** as pdf or png at 13x18 cm
- **Simple and intuitive user interface**

## 🌐 Supported Languages
- English

> Additional languages will be added in future releases

## 📝 Dependencies & Versions

**Maven Plugin & Java**
- Maven Plugin: 3.13.0
- Java: 21.0.2

**Libraries**
> All libraries are configured in `pom.xml`

## 📝 How to Build

### Steps

```shell
# 1️⃣ Clone repository
git clone https://github.com/pintargasper/CardGenerator.git
cd CardGenerator

# 2️⃣ Open project in IntelliJ IDEA
# Import Maven project and sync

# 3️⃣ Build app or run on device
mvn clean javafx:run # for debug build
mvn clean package    # for release build
