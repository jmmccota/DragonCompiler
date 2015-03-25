# Comentário: Início do código de exemplo#
a = 5 x 3,2
b = (8 + 7) x 4,1
c = 91 - 3 * 2.4
d = 41 : (4 / 2)
e = (a * (((b x c) : d) / 2))
#
se a > 10 então
   tela.mensagem = "A é maior que 10"
fim-se

se b < 30 então
   tela.mensagem2 = "B é menor que 30"
fim-se

se (c <= 40) então
   tela.mensagem3 = "C é menor ou igual a 40"
fim-se

se (d >= 20) e (d =< 60) então
   tela.mensagem4 = "D está entre 20 e 60"
fim-se

se (e =< 30,5) e (e != 20) então
   tela.mensagem5 = "E é menor que 30 e meio, mas é diferente de 20"
   se (e == 0) então
     tela.mensagem5 = "E é zero"
   senão
   se (e < 0) então
     tela.mensagem5 = "E é negativo"
     apaga(tela.mensagem, telamensagem2, telamensagem3, telamensagem4)
   fim-se
   fim-se
fim-se

fat = 1
para i de 1 até 10 faça
    fat = fat x i
fim-para
tela = "10! = " + fat

fat = 1
i = 1
enquanto i <= 5 faça
    fat = fat x i
    i = i + 1
fim-enquanto
tela = "5! = " + fat

vetor m[10][10]

m[1][1] = 1.5
m[1][2] = 3.2
m[2][1] = 3,3
m[2][i] = 2,9
#
# As outras posições não foram inicializadas
Portanto há zero ou espaço no seu interior #