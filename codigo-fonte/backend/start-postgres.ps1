param(
    # Host do PostgreSQL que o backend vai usar para abrir a conexão.
    [string]$DbHost = "localhost",
    # Porta do PostgreSQL no ambiente local ou remoto.
    [int]$DbPort = 5432,
    # Nome do banco de dados que será usado pelo Spring Boot.
    [string]$DbName = "calculos_judiciais",
    # Usuário do banco que será exposto ao processo do backend.
    [string]$DbUser = "postgres",
    # Senha aceita como texto puro ou SecureString; o script normaliza o valor depois.
    [object]$DbPassword,
    # Quando ativado, o script pede a senha de forma interativa no terminal.
    [switch]$AskPassword,
    # Quando ativado, encerra automaticamente qualquer processo já preso na porta 8080.
    [switch]$StopExisting8080
)

# Faz o script parar na primeira falha em vez de continuar com um estado inválido.
$ErrorActionPreference = "Stop"

# Converte uma senha protegida em texto simples só no momento de exportar para variáveis de ambiente.
# Isso evita manipular manualmente o conteúdo criptografado em outros pontos do script.
function ConvertFrom-SecureStringToPlainText {
    param([SecureString]$SecureValue)

    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureValue)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    }
}

# Normaliza a senha para texto simples, independentemente de ter vindo como SecureString ou string.
function ConvertTo-PlainTextPassword {
    param([object]$PasswordValue)

    if ($PasswordValue -is [SecureString]) {
        return ConvertFrom-SecureStringToPlainText -SecureValue $PasswordValue
    }

    return [string]$PasswordValue
}

# Se a senha não foi informada por parâmetro, mas o modo interativo foi habilitado,
# o script abre um prompt seguro para receber a senha sem mostrá-la na tela.
if (-not $DbPassword -and $AskPassword) {
    $secure = Read-Host "Informe a senha do PostgreSQL" -AsSecureString
    $DbPassword = $secure
}

# Sem senha não há como montar a conexão com o PostgreSQL, então o script interrompe a execução.
if (-not $DbPassword) {
    throw "Informe a senha via -DbPassword ou use -AskPassword."
}

# Converte a senha para texto simples para repassá-la ao processo filho do Maven/Spring Boot.
$DbPasswordPlainText = ConvertTo-PlainTextPassword -PasswordValue $DbPassword

# Variável de ambiente consumida pelo Spring Boot para apontar para o banco correto.
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://$($DbHost):$($DbPort)/$($DbName)"
# Variável de ambiente com o usuário do banco usada pela configuração do datasource.
$env:SPRING_DATASOURCE_USERNAME = $DbUser
# Variável de ambiente com a senha do banco usada pela configuração do datasource.
$env:SPRING_DATASOURCE_PASSWORD = $DbPasswordPlainText
# Forma alternativa equivalente de montar a URL, deixada aqui como referência.
# $env:SPRING_DATASOURCE_URL = "jdbc:postgresql://${DbHost}:${DbPort}/${DbName}"

# Mensagem de contexto para confirmar no terminal qual banco será usado.
Write-Host "Iniciando backend com PostgreSQL em $($DbHost):$($DbPort)/$($DbName)" -ForegroundColor Cyan
# Mensagem de contexto com o usuário que será enviado ao datasource.
Write-Host "Usuario: $DbUser" -ForegroundColor Cyan

# Verifica se já existe algum processo escutando na porta padrão da aplicação.
$listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($listener) {
    # Descobre qual processo está ocupando a porta 8080 para evitar conflito na inicialização.
    $owningPid = $listener.OwningProcess
    $existing = Get-Process -Id $owningPid -ErrorAction SilentlyContinue
    $existingName = if ($existing) { $existing.ProcessName } else { "desconhecido" }

    # Sem a flag de parada automática, o script interrompe para não derrubar algo por acidente.
    if (-not $StopExisting8080) {
        Write-Error "A porta 8080 ja esta em uso por PID $owningPid ($existingName). Use -StopExisting8080 para encerrar o processo automaticamente."
    }

    # Com a flag habilitada, encerra o processo antigo para liberar a porta para o backend.
    Write-Host "Encerrando processo na porta 8080: PID $owningPid ($existingName)" -ForegroundColor Yellow
    Stop-Process -Id $owningPid -Force
}

# Inicia o backend com Maven usando a configuração de ambiente montada acima.
mvn spring-boot:run
