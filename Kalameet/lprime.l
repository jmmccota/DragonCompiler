# Coment�rio: In�cio do c�digo de exemplo#
a = 5 x 3,2
b = (8 + 7) x 4,1
c = 91 - 3 * 2.4
d = 41 : (4 / 2)
e = (a * (((b x c) : d) / 2))

se a > 10 ent�o
   tela.mensagem = "A � maior que 10"
fim-se

se b < 30 ent�o
   tela.mensagem2 = "B � menor que 30"
fim-se

se (c <= 40) ent�o
   tela.mensagem3 = "C � menor ou igual a 40"
fim-se

se (d >= 20) e (d =< 60) ent�o
   tela.mensagem4 = "D est� entre 20 e 60"
fim-se

se (e =< 30,5) e (e != 20) ent�o
   tela.mensagem5 = "E � menor que 30 e meio, mas � diferente de 20"
   se (e == 0) ent�o
     tela.mensagem5 = "E � zero"
   sen�o
   se (e < 0) ent�o
     tela.mensagem5 = "E � negativo"
     apaga(tela.mensagem, telamensagem2, telamensagem3, telamensagem4)
   fim-se
   fim-se
fim-se

fat = 1
para i de 1 at� 10 fa�a
    fat = fat x i
fim-para
tela = "10! = " + fat

fat = 1
i = 1
enquanto i <= 5 fa�a
    fat = fat x i
    i = i + 1
fim-enquanto
tela = "5! = " + fat

vetor m[10][10]

m[1][1] = 1.5
m[1][2] = 3.2
m[2][1] = 3,3
m[2][i] = 2,9

fun(a,b,fun2((4,15+(7+d)), (8.9+e)))
# As outras posi��es n�o foram inicializadas
Portanto h� zero ou espa�o no seu interior #
fim