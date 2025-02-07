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

FUNÇÃO treinar(instancia):
    votos ← classificador.obter_votos_para(instancia)
    
    SE votos NÃO VAZIO:
        indice_classe_predita ← índice da maior probabilidade em votos
        confiança ← votos[indice_classe_predita] / soma(votos)
    SENÃO:
        índice_classe_predita ← NULO
        confiança ← 0

    classe_real ← valor_da_classe(instancia)

    SE confiança < 0.75 OU indice_classe_predita ≠ classe_real:
        classificador.treinar(instancia)

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
