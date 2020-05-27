package com.jalo;


import java.io.*;
import java.time.Year;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

  public static void main(String[] args) throws IOException {
    Scanner terminalInput = new Scanner(System.in);
    String pilihanUser;
    boolean isLanjutkan = true;

    while (isLanjutkan) {
      clearScreen();
      System.out.println("+=================================+");
      System.out.println("|       Databases  Mahasiswa      |");
      System.out.println("+=================================+\n");
      // menu;
      System.out.println("1.\tList Data Mahasiswa.");
      System.out.println("2.\tCari Data Mahasiswa.");
      System.out.println("3.\tTambah Data Mahasiswa.");
      System.out.println("4.\tUbah Data Mahasiswa.");
      System.out.println("5.\tHapus Data Mahasiswa.");

      System.out.print("\n\nMasukan pilihan anda : ");
      pilihanUser = terminalInput.next();
      switch (pilihanUser) {
        case "1":
          // tampil data
          System.out.println("+=====================+");
          System.out.println("| List Data Mahasiswa |");
          System.out.println("+=====================+");
          tampilData();
          break;
        case "2":
          // cari data
          System.out.println("+=======================+");
          System.out.println("| Search Data Mahasiswa |");
          System.out.println("+=======================+");
          cariData();
          break;
        case "3":
          // tambah data
          System.out.println("+=======================+");
          System.out.println("| Tambah Data Mahasiswa |");
          System.out.println("+=======================+");
          tambahData();
          break;
        case "4":
          // ubah data
          System.out.println("+=====================+");
          System.out.println("| Ubah Data Mahasiswa |");
          System.out.println("+=====================+");
          updateData();
          break;
        case "5":
          //hapus data
          System.out.println("+======================+");
          System.out.println("| Hapus Data Mahasiswa |");
          System.out.println("+======================+");
          deleteData();
          break;
        default:
          System.err.println("\nInput tidak di temukan\npilih input [1-5]");
      }
      isLanjutkan = getYesOrNo("Apakah anda ingin melanjutkan ");
    }
  }

  private  static void updateData()throws IOException{
    // mengambil database original
    File database = new File("databases.txt");
    FileReader fileInput = new FileReader(database);
    BufferedReader bufferedInput = new BufferedReader(fileInput);

    // membuat database sementara
    File tempDB = new File("tempDB.txt");
    FileWriter fileOutput = new FileWriter(tempDB);
    BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);
    // tampilkan data
    System.out.println("\nList data Mahasiswa");
    tampilData();

    // pilihan data
    Scanner terminalInput = new Scanner(System.in);
    System.out.print("\nMasukan nomor Mahasiswa yang ingin di update: ");
    int updateNum = terminalInput.nextInt();

    // tampilkan data data update
    String data = bufferedInput.readLine();
    int entryCounts = 0;
    while(data != null){
      entryCounts++;

      StringTokenizer st = new StringTokenizer(data, ",");

      // tampilkan entrycounts == updateNum
      if (updateNum == entryCounts){
        System.out.println("\n\tData yang akan di update : ");
        System.out.println("=====================================");
        st.nextToken();
        System.out.println("Nama Mahasiswa       : " + st.nextToken());
        System.out.println("Nama Nim             : " + st.nextToken());
        System.out.println("Nama Jurusan         : " + st.nextToken());
        System.out.println("Nama Tahun angkatan  : " + st.nextToken());
        System.out.println("Nama Email           : " + st.nextToken());

        // update data

        // mengambil input user
        String[] fieldData = {"nama","nim","jurusan","tahun","email"};
        String[] tempdata = new String[5];

        st = new StringTokenizer(data, ",");
        String originalData = st.nextToken();
        for (int i=0; i < fieldData.length; i++){
          boolean isUpdate = getYesOrNo("Apakah anda ingin merubah "+fieldData[i]+" Mahasiswa ");

          originalData = st.nextToken();
          if (isUpdate){
            // user input

            if (fieldData[i].equalsIgnoreCase("tahun")){
              System.out.print("Masukan Tahun Angkatan Format [yyyy] : ");
              tempdata[i] = ambilTahun();
            }else {
              terminalInput = new Scanner(System.in);
              System.out.print("\nMasukan "+fieldData[i]+" baru :");
              tempdata[i] = terminalInput.nextLine();
            }
          }else {
            tempdata[i] = originalData;
          }
        }
        // tampilkan data baru ke layar
        st = new StringTokenizer(data, ",");
        System.out.println("\n\tData baru Mahasiswa adalah : ");
        System.out.println("=====================================");
        st.nextToken();
        System.out.println("Nama Mahasiswa       : " + st.nextToken() + " --> " + tempdata[0]);
        System.out.println("Nama Nim             : " + st.nextToken() + " --> " + tempdata[1]);
        System.out.println("Nama Jurusan         : " + st.nextToken() + " --> " + tempdata[2]);
        System.out.println("Nama Tahun angkatan  : " + st.nextToken() + " --> " + tempdata[3]);
        System.out.println("Nama Email           : " + st.nextToken() + " --> " + tempdata[4]);

        boolean isUpdate = getYesOrNo("Apakah anda sudah yakin merubah data ");
        if (isUpdate){
          // cek data baru di database
          boolean isExist = cekMhs(tempdata,false);

          if (isExist){
            System.err.println("Data mahasiswa sudah ada di database\nProses data di batalkan !!");
            bufferedOutput.write(data);
          }else {
            // format data baru ke database
            String nama = tempdata[0];
            String nim = tempdata[1];
            String jurusan = tempdata[2];
            String tahun = tempdata[3];
            String email = tempdata[4];

            // primary key
            String primaryKey = nim + "_" + tahun;
            // tulis data database
            bufferedOutput.write(primaryKey + ","+nama+","+nim+","+jurusan+","+tahun+","+email);
          }
        }else {
          // copy data
          bufferedOutput.write(data);
        }
      }else {
        // copy data
        bufferedOutput.write(data);
      }
      bufferedOutput.newLine();
      data = bufferedInput.readLine();
    }
    bufferedOutput.flush();
    bufferedOutput.close();
    fileOutput.close();
    bufferedInput.close();
    fileInput.close();

    System.gc();

    // delete original file
    database.delete();
    // rename file sementara ke database
    tempDB.renameTo(database);

  }

  // delete data
  private static void deleteData()throws IOException {
    // ambil database original
    File database = new File("databases.txt");
    FileReader fileInput = new FileReader(database);
    BufferedReader bufferedInput = new BufferedReader(fileInput);

    // buat database sementara
    File tempDB = new File("tempDB.txt");
    FileWriter fileOutput = new FileWriter(tempDB);
    BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

    // tampilkan data
    System.out.println("Data Mahasiswa");
    tampilData();

    // ambil userInput

    Scanner terminalInput = new Scanner(System.in);
    System.out.print("\nMasukan nomor Mahasiswa yang ingin di hapus : ");
    int deleteNum = terminalInput.nextInt();
    // looping tiap baris dan skip yang akan di delete

    boolean isFouund = false;
    int entryCount = 0;
    String data = bufferedInput.readLine();

    while (data != null){
      entryCount++;
      boolean isDelete = false;

      StringTokenizer st = new StringTokenizer(data, ",");

      // tampilkan data yang akan di hapus
      if (deleteNum == entryCount){
        System.out.println("\n\tData yang akan di hapus : ");
        System.out.println("=====================================");
        st.nextToken();
        System.out.println("Nama Mahasiswa       : " + st.nextToken());
        System.out.println("Nama Nim             : " + st.nextToken());
        System.out.println("Nama Jurusan         : " + st.nextToken());
        System.out.println("Nama Tahun angkatan  : " + st.nextToken());
        System.out.println("Nama Email           : " + st.nextToken());

        isDelete = getYesOrNo("Apakah anda yakin ingin menghapus data ");
        isFouund = true;
      }
      if (isDelete){
        // skip pindahkan data dari data original ke sementara
        System.out.println("Data berhasil di Hapus");
      }else {
        // pindahkan data dari data original ke sementara
        bufferOutput.write(data);
        bufferOutput.newLine();
      }
      data = bufferedInput.readLine();
    }
    if (!isFouund){
      System.err.println("Data tidak ditemukan!!");
    }

    // menulis data ke file
    bufferOutput.flush();
    bufferOutput.close();
    fileOutput.close();
    bufferedInput.close();
    fileInput.close();

    System.gc();

    // delete original file
    database.delete();
    // rename file sementara ke database
    tempDB.renameTo(new File("databases.txt"));
  }

  private static void tambahData()throws  IOException{
    FileWriter fileOutput = new FileWriter("databases.txt",true);
    BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

    // mengambil input dari user
    Scanner terminalInput = new Scanner(System.in);
    String nama, nim, jurusan, tahun, email;

      System.out.print("Masukan nama Mahasiswa : ");
      nama = terminalInput.nextLine();
      System.out.print("Masukan Nim Mahasiswa : ");
      nim = terminalInput.nextLine();
      System.out.print("Masukan Jurusan Mahasiswa : ");
      jurusan= terminalInput.nextLine();
      System.out.print("Masukan Tahun Angkatan Format [yyyy] : ");
      tahun = ambilTahun();
      System.out.print("Masukan Alamat Email : ");
      email = terminalInput.nextLine();

    String[] keywords = {nama+","+nim+","+jurusan+","+tahun+","+email};
    System.out.println(Arrays.toString(keywords));

    boolean isExist = cekMhs(keywords,false);

    // menulis data mahasiswa
    if (!isExist){

//      muhamad reza,2408200047,teknik informatika,2018,rezacahyono@gmail.com

//      String namaWithoutspace = nama.replaceAll("\\s","");
      String primaryKey = nim + "_" + tahun;
      System.out.println("\n  Data mahasiswa yang anda masukan sebagai berikut : ");
      System.out.println("------------------------------------------------------");
      System.out.println("Primary key     : " + primaryKey);
      System.out.println("Nama Mahasiswa  : " + nama);
      System.out.println("Nim             : " + nim);
      System.out.println("Jurusan         : " + jurusan);
      System.out.println("Tahun angkatan  : " + tahun);
      System.out.println("Alamat emal     : " + email);

      boolean isTambah = getYesOrNo("Apakah anda yakin tambah data mahasiswa ");

      if (isTambah){
        bufferOutput.write(primaryKey + ","+nama+","+nim+","+jurusan+","+tahun+","+email);
        bufferOutput.newLine();
        bufferOutput.flush();
      }
      tampilData();
    }else {
      System.out.println("\nData mahasiswa yang anda ingin tambah sudah tersedia di databases,\ndengan data berikut :");
      cekMhs(keywords,true);
    }

    // cek buku di database
    bufferOutput.close();

  }

  // ambil tahun dengan format [yyyy]
  private static String ambilTahun()throws IOException{
    boolean tahunValid = false;
    Scanner terminalInput = new Scanner(System.in);
    String tahunInput = terminalInput.nextLine();

    while (!tahunValid) {
      try {
        Year.parse(tahunInput);
        tahunValid = true;
      } catch (Exception e) {
        System.out.println("\nFormat tahun salah");
        System.out.print("Masukan tahun lagi : ");
        tahunValid = false;
        tahunInput = terminalInput.nextLine();
      }
    }
    return tahunInput;
  }

  // tambil data
  private static void tampilData() throws IOException{

    FileReader fileInput;
    BufferedReader bufferInput;

    // membaca databases
    try {
      fileInput = new FileReader("databases.txt");
      bufferInput = new BufferedReader(fileInput);
    }catch (Exception e){
      System.err.println("Databases tidak ditemukan!!");
      System.err.println("Silahkan tambah data terlebih dahulu");
      System.out.println("-------------------------------------");
      tambahData();
      return;
    }


    System.out.print("==============================================================================================================+");
    System.out.println("\n| No |\tNama              |\tNIM       |\tJurusan            |\tTahun |\tEmail                         |");
    System.out.println("==============================================================================================================+");
    String data = bufferInput.readLine();
    int nomorMhs = 0;
    while (data != null) {
      // id increment
      nomorMhs++;

      // pemisah degan delimiternya ","
      StringTokenizer stringToken = new StringTokenizer(data, ",");
      stringToken.nextToken();
      System.out.printf("| %2d ", nomorMhs);
      System.out.printf("|\t%-17s ", stringToken.nextToken());
      System.out.printf("|\t%s", stringToken.nextToken());
      System.out.printf("|\t%-19s", stringToken.nextToken());
      System.out.printf("|\t%-6s", stringToken.nextToken());
      System.out.printf("|\t%-30s|", stringToken.nextToken());
      System.out.print("\n");

      data = bufferInput.readLine();
    }
    System.out.println("==============================================================================================================+");

  }

  // pilihan get yes or no
  private static boolean getYesOrNo(String message){
    Scanner terminalInput = new Scanner(System.in);
    System.out.print("\n"+message+"[y/n] ? ");
    String pilihanUser = terminalInput.next();

    while (!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")){
      System.err.println("\nPilihan anda bukan (y atau n)!!");
      System.out.print("\n"+message+"[y/n] ? ");
      pilihanUser = terminalInput.next();
    }

    return pilihanUser.equalsIgnoreCase("y");
  }

  // clear screen
  private static void clearScreen(){
    try {
      if (System.getProperty ("os.name").contains("Windows")){
        new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
      }else {
        System.out.print("\033\143");
      }
    }catch (Exception ex){
      System.err.println("Tidak bisa clear screen");
    }
  }

  // cari data mahasiswa
  private static void cariData()throws IOException{
    // membaca ada atau tidak
    try {

      File file = new File("databases.txt");
    }catch (Exception e){
      System.err.println("Databases tidak ditemukan!!");
      System.err.println("Silahkan tambah data terlebih dahulu");
      tambahData();
      return;
    }
    // keyword dari user
    Scanner terminalInput = new Scanner(System.in);
    System.out.print("Masukan kata kunci Mahsiswa : ");
    String cari = terminalInput.nextLine();

    String[] keywords = cari.split("\\s+");

    // cek keyword dari database

    cekMhs(keywords,true);

  }

  // cek data mahasiswa
  private static boolean cekMhs(String[] keywords, boolean isDisplay) throws IOException{

    FileReader fileInput = new FileReader("databases.txt");
    BufferedReader bufferInput = new BufferedReader(fileInput);

    String data = bufferInput.readLine();
    boolean isExist = false;
    int nomorMhs = 0;
    if (isDisplay) {
      System.out.print("======================================================================================================+");
      System.out.println("\n| No |\tNama              |\tNIM       |\tJurusan            |\tTahun |\tEmail                 |");
      System.out.println("======================================================================================================+");
    }

    while (data != null){
      // cek keyword dalam baris
      isExist = true;
      for (String keyword:keywords) {
        isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
      }

      // jika keywords cocok, tampilkan

      if (isExist){
        if (isDisplay) {
          nomorMhs++;
          StringTokenizer stringToken = new StringTokenizer(data, ",");
          stringToken.nextToken();
          System.out.printf("| %2d ", nomorMhs);
          System.out.printf("|\t%-17s ", stringToken.nextToken());
          System.out.printf("|\t%s", stringToken.nextToken());
          System.out.printf("|\t%-19s", stringToken.nextToken());
          System.out.printf("|\t%-6s", stringToken.nextToken());
          System.out.printf("|\t%-22s|", stringToken.nextToken());
          System.out.print("\n");

        }else {
          break;
        }
      }
      data = bufferInput.readLine();

    }
    if (isDisplay) {
      System.out.println("======================================================================================================+");
    }
    return isExist;
  }
}














