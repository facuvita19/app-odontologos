#define MyAppName "Sistema Odontologico"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "Facundo Vitale"
#define MyAppExeName "Sistema Odontologico.exe"

[Setup]
AppId={{F5E6A68A-5A4B-49F1-8B32-1DECF529F178}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={localappdata}\{#MyAppName}
DefaultGroupName={#MyAppName}
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
OutputDir=package
OutputBaseFilename=Instalador-App-Odontologos-v1.0.0
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
UninstallDisplayName={#MyAppName}
UninstallDisplayIcon={app}\{#MyAppExeName}
SetupLogging=yes

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "Crear un acceso directo en el escritorio"; GroupDescription: "Accesos directos:"; Flags: unchecked

[Files]
Source: "output\Sistema Odontologico\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "input\database.properties.example"; DestDir: "{app}"; Flags: ignoreversion
Source: "input\README.md"; DestDir: "{app}"; Flags: ignoreversion
Source: "input\LICENSE"; DestDir: "{app}"; Flags: ignoreversion
Source: "LEEME-INSTALACION.txt"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"
Name: "{group}\Desinstalar {#MyAppName}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "{app}"; Description: "Abrir la carpeta de instalacion para configurar la base de datos"; Flags: postinstall shellexec skipifsilent

[Code]
function InitializeSetup(): Boolean;
begin
  Result := True;
end;
