#!/bin/bash

# 1. Compilar o projeto Maven
echo "Compilando o projeto Maven..."
mvn clean install

# Verificar se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro: Falha na compilação do Maven."
    exit 1
fi

# 2. Obter o nome do JAR gerado
JAR_NAME=$(ls target/*.jar | head -n 1)

if [ -z "$JAR_NAME" ]; then
    echo "Erro: JAR não encontrado em target/."
    exit 1
fi

echo "JAR gerado: $JAR_NAME"

# 3. Compilar a classe de teste
echo "Compilando a classe de teste TesteExecucao.java..."
javac -cp "$JAR_NAME":. TesteExecucao.java

if [ $? -ne 0 ]; then
    echo "Erro: Falha na compilação de TesteExecucao.java."
    exit 1
fi

# 4. Executar a classe de teste
echo "Executando o teste..."
java -cp "$JAR_NAME":. TesteExecucao

# 5. Limpar arquivos de classe gerados
rm -f TesteExecucao.class

echo "Execução concluída."
