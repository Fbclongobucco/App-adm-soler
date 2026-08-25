#!/bin/bash
# Script de testes para a API adm_soler
# Servidor rodando na porta 8080

BASE_URL="http://localhost:8080/api/v1"

# Credenciais do admin semeado no start (security.bootstrap-admin).
ADMIN_EMAIL="${ADMIN_EMAIL:-email@soler.com.br}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-1234567}"

echo "========================================="
echo "  TESTES DA API ADM_SOLER"
echo "========================================="

# ==========================================
# LOGIN - a API e stateless: todo endpoint
# abaixo exige Authorization: Bearer <token>
# ==========================================
echo -e "\n--- Autenticando como ADMIN ---"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASSWORD\"}")

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Falha no login. Resposta: $LOGIN_RESPONSE"
  exit 1
fi
echo "Autenticado. Access token obtido."

AUTH="Authorization: Bearer $TOKEN"

# 1. ADDRESS
echo -e "\n--- 1. Criando Address ---"
ADDRESS_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/addresses" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "Rua das Flores",
    "number": "123",
    "complement": "Sala 1",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01234-567",
    "country": "Brasil"
  }')
echo "$ADDRESS_RESPONSE"
ADDRESS_ID=$(echo $ADDRESS_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Address ID: $ADDRESS_ID"

# 2. USER
echo -e "\n--- 2. Criando User ---"
USER_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "123456",
    "phone": "11999990000"
  }')
echo "$USER_RESPONSE"
USER_ID=$(echo $USER_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "User ID: $USER_ID"

# 3. EQUIPMENT
echo -e "\n--- 3. Criando Equipment ---"
EQUIPMENT_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/equipments" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell",
    "description": "Notebook para desenvolvimento"
  }')
echo "$EQUIPMENT_RESPONSE"
EQUIPMENT_ID=$(echo $EQUIPMENT_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Equipment ID: $EQUIPMENT_ID"

# 4. CLIENT (depende de Address)
echo -e "\n--- 4. Criando Client ---"
CLIENT_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/clients" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Empresa ABC Ltda\",
    \"email\": \"contato@abc.com\",
    \"phone\": \"1133334444\",
    \"cnpj\": \"12.345.678/0001-90\",
    \"addressId\": \"$ADDRESS_ID\"
  }")
echo "$CLIENT_RESPONSE"
CLIENT_ID=$(echo $CLIENT_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Client ID: $CLIENT_ID"

# 5. EMPLOYEE (depende de Address)
echo -e "\n--- 5. Criando Employee ---"
EMPLOYEE_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/employees" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Maria Santos\",
    \"email\": \"maria@email.com\",
    \"phone\": \"11988887777\",
    \"addressId\": \"$ADDRESS_ID\",
    \"role\": \"Desenvolvedora\"
  }")
echo "$EMPLOYEE_RESPONSE"
EMPLOYEE_ID=$(echo $EMPLOYEE_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Employee ID: $EMPLOYEE_ID"

# 6. PROJECT (depende de Client)
echo -e "\n--- 6. Criando Project ---"
PROJECT_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -d "{
    \"os\": \"OS-2024-001\",
    \"serviceProvided\": \"Desenvolvimento de sistema web\",
    \"clientId\": \"$CLIENT_ID\",
    \"startDate\": \"2024-01-15T08:00:00\",
    \"endDate\": \"2024-06-30T18:00:00\"
  }")
echo "$PROJECT_RESPONSE"
PROJECT_ID=$(echo $PROJECT_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Project ID: $PROJECT_ID"

# 7. RESTAURANT (depende de Address + Project)
echo -e "\n--- 7. Criando Restaurant ---"
RESTAURANT_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/restaurants" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Restaurante Sabor da Terra\",
    \"email\": \"contato@sabordaterra.com\",
    \"phone\": \"1155556666\",
    \"cnpj\": \"98.765.432/0001-10\",
    \"projectId\": \"$PROJECT_ID\",
    \"isBilled\": true,
    \"lunchPrice\": 35.00,
    \"dinnerPrice\": 45.00,
    \"additionalValues\": 10.00,
    \"days\": 30,
    \"addressId\": \"$ADDRESS_ID\"
  }")
echo "$RESTAURANT_RESPONSE"
RESTAURANT_ID=$(echo $RESTAURANT_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Restaurant ID: $RESTAURANT_ID"

# 8. ACCOMMODATION (depende de Address + Project)
echo -e "\n--- 8. Criando Accommodation ---"
ACCOMMODATION_RESPONSE=$(curl -s -H "$AUTH" -X POST "$BASE_URL/accommodations" \
  -H "Content-Type: application/json" \
  -d "{
    \"addressId\": \"$ADDRESS_ID\",
    \"projectId\": \"$PROJECT_ID\",
    \"capacity\": 10,
    \"startDate\": \"2024-01-15T14:00:00\",
    \"endDate\": \"2024-06-30T12:00:00\"
  }")
echo "$ACCOMMODATION_RESPONSE"
ACCOMMODATION_ID=$(echo $ACCOMMODATION_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Accommodation ID: $ACCOMMODATION_ID"

# ==========================================
# TESTES GET
# ==========================================
echo -e "\n========================================="
echo "  TESTES GET (LISTAR TODOS)"
echo "========================================="

echo -e "\n--- Listando Addresses ---"
curl -s -H "$AUTH" "$BASE_URL/addresses" | python3 -m json.tool

echo -e "\n--- Listando Users ---"
curl -s -H "$AUTH" "$BASE_URL/users" | python3 -m json.tool

echo -e "\n--- Listando Equipments ---"
curl -s -H "$AUTH" "$BASE_URL/equipments" | python3 -m json.tool

echo -e "\n--- Listando Clients ---"
curl -s -H "$AUTH" "$BASE_URL/clients" | python3 -m json.tool

echo -e "\n--- Listando Employees ---"
curl -s -H "$AUTH" "$BASE_URL/employees" | python3 -m json.tool

echo -e "\n--- Listando Projects ---"
curl -s -H "$AUTH" "$BASE_URL/projects" | python3 -m json.tool

echo -e "\n--- Listando Restaurants ---"
curl -s -H "$AUTH" "$BASE_URL/restaurants" | python3 -m json.tool

echo -e "\n--- Listando Accommodations ---"
curl -s -H "$AUTH" "$BASE_URL/accommodations" | python3 -m json.tool

# ==========================================
# TESTES GET POR ID
# ==========================================
echo -e "\n========================================="
echo "  TESTES GET POR ID"
echo "========================================="

echo -e "\n--- Buscando Address por ID ---"
curl -s -H "$AUTH" "$BASE_URL/addresses/$ADDRESS_ID" | python3 -m json.tool

echo -e "\n--- Buscando Project por ID ---"
curl -s -H "$AUTH" "$BASE_URL/projects/$PROJECT_ID" | python3 -m json.tool

# ==========================================
# TESTE PUT (ATUALIZAR)
# ==========================================
echo -e "\n========================================="
echo "  TESTE PUT (ATUALIZAR)"
echo "========================================="

echo -e "\n--- Atualizando Address ---"
curl -s -H "$AUTH" -X PUT "$BASE_URL/addresses/$ADDRESS_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "Rua das Flores",
    "number": "456",
    "complement": "Sala 2",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01234-567",
    "country": "Brasil"
  }' | python3 -m json.tool

# ==========================================
# TESTE DELETE
# ==========================================
echo -e "\n========================================="
echo "  TESTE DELETE"
echo "========================================="

echo -e "\n--- Deletando Equipment ---"
curl -s -H "$AUTH" -w "HTTP Status: %{http_code}\n" -X DELETE "$BASE_URL/equipments/$EQUIPMENT_ID"

echo -e "\n--- Verificando se Equipment foi deletado ---"
curl -s -H "$AUTH" "$BASE_URL/equipments" | python3 -m json.tool

# ==========================================
# TESTES DE AUTENTICACAO
# ==========================================
echo -e "\n========================================="
echo "  TESTES DE AUTENTICACAO"
echo "========================================="

echo -e "\n--- Usuario autenticado (GET /auth/me) ---"
curl -s -H "$AUTH" "$BASE_URL/auth/me" | python3 -m json.tool

echo -e "\n--- Sem token (esperado 401) ---"
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" "$BASE_URL/users"

echo -e "\n--- Renovando a sessao (POST /auth/refresh) ---"
REFRESH_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")
NEW_REFRESH=$(echo "$REFRESH_RESPONSE" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)
echo "Refresh token rotacionado: ${NEW_REFRESH:0:12}..."

echo -e "\n--- Reusando o refresh token antigo (esperado 401) ---"
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" -X POST "$BASE_URL/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"

echo -e "\n========================================="
echo "  TODOS OS TESTES CONCLUIDOS!"
echo "========================================="
