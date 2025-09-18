#define ProjectRoot "..\"

#define AppVersion "v1.0.0"
#define AppVersionNumber "1.0.0"
#define AppVersionFileName "1.0.0"

#define AppName "Card Generator"
#define AppExeName "Card Generator.exe"
#define AppNameNoSpace "CardGenerator"
#define AppNameLower "cardgenerator"
#define AppPublisher "Gašper Pintar"
#define AppUrl "https://cards.gasperpintar.com"
#define AppDateYear GetDateTimeString('yyyy', '', '')
#define AppCopyRight "Copyright (c) Gašper Pintar -" + AppDateYear
#define AppDescription "Card Generator allows the creation of any card and also its generation in png format. It also allows downloading images in pdf mode"

#define LanguageGlobal "global"

[Setup]
SignTool=card-generator
AppId={{E2C81343-9913-4AF8-8AEA-CD365129D612}
AppName={#AppName}
AppVersion={#AppVersion}
AppVerName={#AppName} {#AppVersionFileName}
AppPublisher={#AppPublisher}
AppPublisherURL={#AppUrl}
AppSupportURL={#AppUrl}
AppUpdatesURL={#AppUrl}
AppCopyright={#AppCopyRight}

DefaultDirName={localappdata}\Programs\{#AppName}
DefaultGroupName={#AppName}

SetupIconFile={#ProjectRoot}\src\main\resources\com\gasperpintar\cardgenerator\images\logo.ico
WizardImageFile={#ProjectRoot}\src\main\resources\com\gasperpintar\cardgenerator\images\logo.bmp
WizardSmallImageFile={#ProjectRoot}\src\main\resources\com\gasperpintar\cardgenerator\images\logo.bmp
WizardStyle=modern

AllowNoIcons=yes
DisableDirPage=no
OutputDir={#ProjectRoot}\dev\output\windows\setup\
OutputBaseFilename={#AppName}-setup

Compression=lzma2/ultra
SolidCompression=yes

ShowTasksTreeLines=Yes
DisableProgramGroupPage=True

PrivilegesRequired=lowest
ArchitecturesAllowed=x86compatible
ArchitecturesInstallIn64BitMode=x64compatible

VersionInfoCompany={#AppPublisher}
VersionInfoDescription={#AppDescription}
VersionInfoVersion={#AppVersionNumber}
VersionInfoCopyright={#AppCopyRight}

UninstallDisplayName={#AppName}
UninstallDisplayIcon={app}\{#AppExeName}
UninstallFilesDir={app}\uninstall
Uninstallable=yes
AllowCancelDuringInstall=yes

[Files]
Source: "{#ProjectRoot}\dev\output\windows\Card Generator\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{userdesktop}\{#AppName}"; Filename: "{app}\{#AppExeName}"; Tasks: desktopicon
Name: "{userstartup}\{#AppName}"; Filename: "{app}\{#AppExeName}"; Tasks: startupicon
Name: "{group}\{#AppName}"; Filename: "{app}\{#AppExeName}"
Name: "{group}\Visit Card Generator"; Filename: "{#AppUrl}"; IconFilename: "{app}\{#AppExeName}"

[Tasks]
Name: "desktopicon"; Description: "Create a desktop icon";
Name: "startupicon"; Description: "Create a startup icon";

[INI]
Filename: "{app}\languages\{#LanguageGlobal}.ini"; Section: "Global"; Key: "LanguageCode"; String: "EN"; Languages: english

[Run]
Filename: "{app}\{#AppExeName}"; WorkingDir: "{app}"; Description: "Run application"; Flags: nowait postinstall skipifsilent

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"; LicenseFile: "{#ProjectRoot}\dev\files\LICENSE.txt";

[UninstallDelete]
Type: filesandordirs; Name: "{app}"
