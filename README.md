# InstanceSelectedClassifier3 - Explicação e Pseudocódigo

## Introdução
O `InstanceSelectedClassifier3` é um classificador projetado para lidar com **concept drift** em fluxos de dados, sendo baseado no "InstanceSelectedClassifier" nativo do MOA. Ele envolve o treinamento de um modelo base e a substituição dele quando a confiança das previsões cai abaixo de um limite. Esse classificador faz parte do framework **MOA (Massive Online Analysis)** e é usado para aprendizado de máquina online.

---

## Como Funciona

1. **Inicialização**:  
   - Cria um classificador base e um novo classificador como cópia dele.
   - Inicializa variáveis de controle (`change_detected`, `warning_detected`).

2. **Resetar aprendizado**:  
   - Cria novos classificadores e os reinicia, garantindo que o modelo possa ser atualizado.

3. **Treinamento**:  
   - Obtém os votos do classificador para uma instância.
   - Calcula a classe predita e a confiança da predição.
   - Se a confiança for baixa (<75%) ou a predição estiver errada, treina o classificador.

4. **Obtenção de votos**:  
   - Retorna as previsões do modelo para uma instância específica.

5. **Medição do modelo**:  
   - Retorna métricas de desempenho do modelo e reseta os contadores de detecção de mudanças.

6. **Definir capacidades**:  
   - Define as capacidades do modelo para indicar que ele pode ser visualizado em diferentes formatos.

---

## Pseudocódigo
```plaintext
INICIAR classificador base com o algoritmo escolhido (ex: Naive Bayes)
INICIAR novo_classificador como uma cópia do classificador base
novo_classificador_reset ← falso
change_detected ← 0
warning_detected ← 0

FUNÇÃO resetar_aprendizado():
    classificador ← nova cópia do classificador base
    novo_classificador ← nova cópia do classificador base
    classificador.reiniciar()
    novo_classificador.reiniciar()
    novo_classificador_reset ← falso

Função trainOnInstanceImpl(instância)
    votos ← obterVotosDoClassificador(instância)

    Se votos não são nulos e tamanho de votos > 0 então
        índicePredito ← índiceDoMaiorValor(votos)
        confiança ← votos[índicePredito] / somaDosVotos(votos)
        
        classeVerdadeira ← obterClasseVerdadeira(instância)

        Se confiança < 0.75 OU índicePredito ≠ classeVerdadeira então
            treinarClassificador(instância)
        
    Senão
        treinarClassificador(instância)  // Treina por segurança


FUNÇÃO obter_votos(instancia):
    RETORNAR classificador.obter_votos_para(instancia)

FUNÇÃO medir_modelo():
    métricas ← obter_métricas_do_classificador()
    change_detected ← 0
    warning_detected ← 0
    RETORNAR métricas

FUNÇÃO definir_capacidades():
    SE esta_classe == InstanceSelectedClassifier3:
        RETORNAR [VIEW_STANDARD, VIEW_LITE]
    SENÃO:
        RETORNAR [VIEW_STANDARD]
```

---

Esse pseudocódigo resume o funcionamento principal do `InstanceSelectedClassifier3`, focando no treinamento baseado na confiança das previsões e na estrutura de detecção de mudanças de conceito (**concept drift**).
O código real esta em https://github.com/Vitor-Izidoro/Criando-uma-Sele-o-de-Instancias/blob/main/InstanceSelectedClassifier3.java (dentro deste mesmo portifólio)


## Explicação focada na Função `trainOnInstanceImpl`

A função `trainOnInstanceImpl` tem o objetivo de treinar o classificador condicionalmente, considerando a confiança da predição. Abaixo está um resumo do seu funcionamento:

1. **Obtém os votos do classificador** para a instância recebida como parâmetro.  
   - Esses votos representam a probabilidade ou pontuação atribuída a cada classe.  

2. **Verifica se há votos disponíveis**.  
   - Se houver votos, a função identifica a classe predita e calcula a confiança da predição.  
   - Se não houver votos, treina o classificador imediatamente.  

3. **Calcula a confiança da predição**, que é a proporção do voto da classe mais provável em relação ao total de votos.  

4. **Compara a confiança com um limiar (75%)** e verifica se a predição está correta.  
   - Se a confiança for menor que 75% **ou** a predição estiver errada, a instância é usada para treinar o classificador.  
   - Caso contrário, a instância é ignorada (não treina).  

Essa abordagem busca melhorar a estabilidade do classificador, evitando aprendizado excessivo com previsões confiantes, mas permitindo correções quando necessário.  

### **Pseudocódigo da Função `trainOnInstanceImpl`**  

```plaintext
Função trainOnInstanceImpl(instância)
    votos ← obterVotosDoClassificador(instância)

    Se votos não são nulos e tamanho de votos > 0 então
        índicePredito ← índiceDoMaiorValor(votos)
        confiança ← votos[índicePredito] / somaDosVotos(votos)
        
        classeVerdadeira ← obterClasseVerdadeira(instância)

        Se confiança < 0.75 OU índicePredito ≠ classeVerdadeira então
            treinarClassificador(instância)
  
    Senão
        treinarClassificador(instância)  // Treina por segurança

```
## Confiança da Predição

A **confiança da predição** representa o quão certo o modelo está sobre a classe que ele escolheu para uma determinada instância. No código, essa confiança é calculada da seguinte maneira:

```java
// Obtém os votos do classificador para a instância
int predictedClass = Utils.maxIndex(votes);
double confidence = votes[predictedClass] / Utils.sum(votes);
```

### **Como funciona?**
- `votes` é um array que contém as probabilidades ou pontuações que o classificador atribuiu a cada classe possível.
- `predictedClass` é o índice da classe com maior probabilidade.
- `Utils.sum(votes)` retorna a soma de todas as probabilidades no vetor `votes`.
- A confiança é calculada dividindo a probabilidade da classe predita pela soma de todas as probabilidades, garantindo um valor entre `0` e `1`.

### **Interpretação**
- **Confiança próxima de 1**: O modelo está muito seguro sobre sua predição.
- **Confiança abaixo de 0.75**: O modelo não está tão certo e pode precisar de mais aprendizado.

### **Exemplo**

Suponha que um modelo esteja classificando imagens entre "Gato", "Cachorro" e "Pássaro", com as seguintes probabilidades:

```plaintext
votes = [0.2, 0.7, 0.1]  // Probabilidades para [Gato, Cachorro, Pássaro]
```

1. O modelo prediz "Cachorro" pois tem a maior probabilidade (`0.7`).
2. A soma das probabilidades é:
   ```
   0.2 + 0.7 + 0.1 = 1.0
   ```
3. A confiança da predição é:
   ```
   0.7 / 1.0 = 0.7
   ```
   Como `0.7 < 0.75`, o modelo decide treinar com essa instância para melhorar sua precisão.

### **Tratamento de Erros**
Se `Utils.sum(votes)` for `0`, isso causaria uma divisão por zero. Para evitar isso, podemos modificar a cálculo da confiança:

```java
double sumVotes = Utils.sum(votes);
if (sumVotes > 0) {
    confidence = votes[predictedClass] / sumVotes;
} else {
    confidence = 0; // Caso especial para evitar erro
}
```

### **Por que verificar a confiança?**
1. **Evita treinamento desnecessário** se o modelo já está certo.
2. **Foca no aprendizado de casos difíceis** onde a confiança é baixa.
3. **Previne overfitting** ao evitar reuso excessivo de instâncias corretas.

Essa lógica melhora a precisão do modelo sem treiná-lo em instâncias desnecessárias.


