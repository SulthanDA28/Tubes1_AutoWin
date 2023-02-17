# Tugas Besar 1 Strategi ALgoritma
## Kelompok Auto Win
<br>

 Disusun oleh:
 - Muhammad Naufal Nalendra (13521152)
 - Sulthan Dzaky Alfaro     (13521159)
 - Muhammad Habibi Husni    (13521169)
 
 <br>

## Algoritma Greedy pada Bot Game Galaxio
 Galaxio adalah sebuah game battle royale yang mempertandingkan bot kapal anda dengan beberapa bot kapal yang lain. Setiap pemain akan memiliki sebuah bot kapal dan tujuan dari permainan adalah agar bot kapal anda yang tetap hidup hingga akhir permainan. Penjelasan lebih lanjut mengenai aturan permainan akan dijelaskan di bawah. Agar dapat memenangkan pertandingan, setiap bot harus mengimplementasikan strategi tertentu untuk dapat memenangkan permainan. 
 <br>
 <br>
 Untuk mengimplementasikan stretegi tersebut digunakanlah algoritma greedy. Algoritma greedy merupakan algoritma mencari nilai yang paling optimal dalam suatu permasalahan. Algoritma ini tidaklah sempurna, karena algoritma ini tidak melihat semua kemungkinan, sehingga nilai yang didapat tidak selalu optimal namun bisa mendekati optimal. Namun algoritma ini memiliki kelebihan yaitu memiliki kompleksitas waktu yang lebih kecil dibandingkan algoritma lain seperti Brute Force. Penggunaan algoritma greedy dalam game Galaxio diimplementasikan untuk mencari stategi terbaik dalam menjalankan bot yang ada di game agar menjadi pemenang. Untuk algoritma greedy yang diimplementasikan dalam strategi kami, kami mengurutkan aksi prioritas dalam menggerakkan bot (kapal) dalam game. Aksi yang digunakan antara lain:
 - Mengindar torpedo, cara menghindar kami dengan cara bergerak ke arah 90 derajat counterclockwise dari arah torpedo menyerang. Selain itu apabila sudah dekat dengan bot, bot akan mengaktifkan afterburner. Apabila size mencukupi, bot akan mengkatifkan shield untuk menghindari serangan musuh. 

 - Menghindari musuh, Apabila size musuh lebih besar dan musuh bergerak dengan cepat dan bot memiliki size yang cukup, bot akan mengaktifkan afterburner. Apabila shield tidak aktif dan size bot mencukupi, bot akan menghindar sekaligus menyerang musuh dengan torpedo. 
 - Kejar musuh, mengejar musuh dilakukan apabila size bot lebih besar dari size musuh terdekat. 

 - Ambil superfood, mengambil superfood terdekat lebih diprioritaskan agar bot bertambah besar lebih cepat. 
 - Ambil makanan, mengambil makanan biasa dilakukan dengan mengambil makanan terdekat 

 - Serang lawan, Menyerang lawan dengan torpedo dilakukan apabila musuh terdekat tidak memakai shield. 
 <br>
 Disini kami mengkombinasikan pertahanan dan serangan. Apabila memang memungkinkan untuk menyerang seperti size bot(kapal) kami lebih dari musuh, kita menyerang. Apabila tidak memungkinkan, kita mencari aman seperti menghindar dari serangan musuh seperti yang ada di atas.
 <br>
 <br>
 
## Requirement untuk Menjalankan Game Galaxio

 <br>
 
 Adapun requirement untuk menjalankan game Galaxio ini. Requirement yang dibutuhkan antara lain:
 - Install .NET Core 3.1 terlebih dahulu sesuai dengan OS yang digunakan. Dapat didownload [disini](https://dotnet.microsoft.com/en-us/download/dotnet/3.1).
 - Install Java minimal Java 11.
 - Unduh starter pack game ini yang dapat diakses [disini](https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2).
 
 <br>

## Cara Menjalankan Bot serta Game pada Game Galaxio

<br>




 
 
 
