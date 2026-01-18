; Inno Setup script for Card Generator

#define src GetEnv('PWD')
; If PWD does not work on all systems, you can use {#src} = CurrentSourceDir

#define url "https://gasperpintar.com"
#define AppUrl "https://gasperpintar.com/card-generator"
#define AppName "Card Generator"
#define AppVersion "1.2.0-alpha+1"
#define AppVersionNumeric "1.2.0"

#include "custom-messages.iss"

[Setup]
AppId={{E2C81343-9913-4AF8-8AEA-CD365129D612}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher=Gasper Pintar
AppPublisherURL={#url}
AppSupportURL={#url}
AppUpdatesURL={#AppUrl}
DefaultDirName={localappdata}\Programs\{#AppName}
DefaultGroupName={#AppName}
AllowNoIcons=yes
SetupIconFile=..\src\main\resources\com\gasperpintar\cardgenerator\images\logo.ico
WizardImageFile=..\src\main\resources\com\gasperpintar\cardgenerator\images\logo.bmp
WizardSmallImageFile=..\src\main\resources\com\gasperpintar\cardgenerator\images\logo.bmp
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
DisableDirPage=no
DisableProgramGroupPage=no
CreateAppDir=yes
CreateUninstallRegKey=yes
Uninstallable=yes
UninstallDisplayName={#AppName}
UninstallDisplayIcon={app}\Card Generator.exe
UninstallFilesDir={app}\uninstall
ArchitecturesInstallIn64BitMode=x64compatible
DisableWelcomePage=no
DisableFinishedPage=no
DisableReadyPage=no
ShowLanguageDialog=yes
WizardStyle=modern
OutputBaseFilename=CardGeneratorSetup
VersionInfoDescription=Card Generator is a desktop application for creating and generating custom playing cards. Using Excel spreadsheets and customizable fxml templates, you can quickly create entire decks of playing cards without having to design each card individually
VersionInfoVersion={#AppVersionNumeric}
VersionInfoCompany=Gasper Pintar
VersionInfoCopyright=Copyright (C) Gasper Pintar

[Languages]
Name: "en"; MessagesFile: "compiler:Default.isl";
Name: "sl"; MessagesFile: "compiler:Languages\Slovenian.isl";

[Files]
Source: "output\windows\Card Generator\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\src\main\resources\com\gasperpintar\cardgenerator\images\logo.ico"; DestDir: "{app}"; Flags: ignoreversion
; Optionally include other needed files (DLLs, config, etc.)

[Icons]
Name: "{group}\{#AppName}"; Filename: "{app}\Card Generator.exe"; WorkingDir: "{app}"
Name: "{group}\Uninstall {#AppName}"; Filename: "{uninstallexe}"
Name: "{userdesktop}\{#AppName}"; Filename: "{app}\Card Generator.exe"; Tasks: desktopicon
Name: "{userstartup}\{#AppName}"; Filename: "{app}\Card Generator.exe"; Tasks: startupicon
Name: "{group}\Visit Card Generator"; Filename: "{#AppUrl}"; IconFilename: "{app}\Card Generator.exe"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"
Name: "startupicon"; Description: "{cm:CreateStartupIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[INI]
Filename: "{app}\languages\global.ini"; Section: "Global"; Key: "LanguageCode"; String: "{language}";

[Run]
Filename: "{app}\Card Generator.exe"; WorkingDir: "{app}"; Description: "Run application"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
Type: filesandordirs; Name: "{app}"
