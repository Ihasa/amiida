package main;

import java.util.Scanner;

import amida.*;

public class Main {
	public static void main(String[] args){
		int vLines = 5;
		Amida amida = new Amida(new String[]{
				"lucky","unlucky","bar","hoge","foo"});
//		amida.addLineH(3);
//		amida.addLineH(2);
//		amida.addLineH(1);
//		amida.addLineH(0);
//		amida.addLineH(0);
//		amida.addLineH(1);
//		amida.addLineH(2);
//		amida.addLineH(2);
//		amida.addLineH(2);
//		amida.addLineH(3);
//		amida.addLineH(1);
//		amida.addLineH(0);
//		Amida amida = new Amida(vLines, new int[]{0, 0, 2, 4});
		printAmida(amida);
		
		Scanner s = new Scanner(System.in);
		while(s.hasNext()){
			String[] input = s.nextLine().split(" ");
			amida.addLineH(Integer.parseInt(input[0]), Integer.parseInt(input[1]), Integer.parseInt(input[2]), Integer.parseInt(input[3]));
			printAmida(amida);
		}
		s.close();
	}
	static void printAmida(Amida amida){
		System.out.println(amida.toString());
		for(int i = 0; i < amida.getLineVNum(); i++)
			System.out.println(i + " => " + amida.getResult(i));
	}
}
