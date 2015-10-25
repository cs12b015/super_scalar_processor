import java.awt.EventQueue;
import java.awt.Font;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import sun.awt.HorizBagLayout;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Design {
	
	
	private int instpercycle;
	private int entrysize;
	private int reorderbuffersize;
	private int storebuffer;
	private int addlatency;
	private int mullatency;
	private int loadlatency;
	private int storelatency;
	private int branchlatency;
	
	private int stall;
	
	private int RSfreeSlots;
	
	private JLabel lblIftxt;
	private JLabel lblIdtxt;
	private JLabel lblRdtxt;
	private JButton btnStart;
	private JButton btnNext;
	private JButton btnQuit;
	private JLabel lblcompletetxt;
	private JLabel lblretiretxt;

	private int pc;
	private JFrame frame;
	
	private ArrayList<JLabel> reslabelmaker = new ArrayList<JLabel> ();
	private ArrayList<JLabel> busylabelmaker = new ArrayList<JLabel> ();
	private ArrayList<JLabel> validlabelmaker = new ArrayList<JLabel> ();
	
	private ArrayList<Integer> resarray = new ArrayList<Integer>();
	private ArrayList<Integer> busyarray = new ArrayList<Integer>();
	private ArrayList<Integer> validarray = new ArrayList<Integer>();
	
	private JLabel lblReservationStation;
	
	private int InstructionCount;
	private ArrayList<String> InstructionSet  = new ArrayList <String>();
	private ArrayList<String> DecodeInstSet = new ArrayList <String>();
	private ArrayList<String> ArchReg = new ArrayList <String>();
	private ArrayList<String> reserveinst = new ArrayList<String>();
	private ArrayList <Integer> stagepc = new ArrayList<Integer>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Design window = new Design();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Design() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("src/input.txt"));		
        String line =  null;
        ArrayList<String> values=new ArrayList<String>();
		while((line=br.readLine())!=null){   
		   values.add(line);
		}
		br.close();
		
		InstructionCount=0;
		br = new BufferedReader(new FileReader("src/Pr_test1.b"));		
        line =  null;
         for (int i = 0; i < 8; i++) {
        	ArchReg.add(stringarf("0","-1","0"));
		}
		while((line=br.readLine())!=null){
		   InstructionSet.add(line);
		   decode idinst = new decode(line);
		   String temp =idinst.getResult();
		   DecodeInstSet.add(temp);
		   InstructionCount++;
		}
		br.close();
		for (int i = 0; i < 8; i++) {
			System.out.print(i+" ");
        	System.out.println(ArchReg.get(i));
		}
		
		System.out.println(DecodeInstSet);
		
		instpercycle= Integer.parseInt(values.get(0)) ;
		entrysize=Integer.parseInt(values.get(1)) ;
		reorderbuffersize=Integer.parseInt(values.get(2)) ;
		storebuffer=Integer.parseInt(values.get(3)) ;
		addlatency=Integer.parseInt(values.get(4)) ;
		mullatency=Integer.parseInt(values.get(5)) ;
		loadlatency=Integer.parseInt(values.get(6)) ;
		storelatency=Integer.parseInt(values.get(7)) ;
		branchlatency=Integer.parseInt(values.get(8)) ;
		
		RSfreeSlots=entrysize;
		for (int i = 0; i < entrysize; i++) {
			reserveinst.add(stringrs("0","-1","-1","-1","-1","-1","-1","0"));
		}
		for (int i = 0; i < entrysize; i++) {
			//reserveinst.add(stringrs("0","-1","-1","-1","-1","-1","-1","0"));
			System.out.print(i + " ");
			System.out.println(reserveinst.get(i));
		}
		
		
		stall=0;
		System.out.println("here it is");
		System.out.println(GetRegNumbers("JMP 33"));
		System.out.println(GetRegNumbers("BEQZ R9 22"));
		System.out.println(GetRegNumbers("HLT"));
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	public static String stringarf(String busy,String tag,String data){
		String res = busy;
		res = res.concat(" ");
		res = res.concat(tag);
		res = res.concat(" ");
		res = res.concat(data);
		return res;
	}
	
	public static String stringrs(String busy,String opcode,String dest,String tag1,String s1,String tag2,String s2,String ready){
		String res = busy;
		res = res.concat(" ");
		res = res.concat(opcode);
		res = res.concat(" ");
		res = res.concat(dest);
		res = res.concat(" ");
		res = res.concat(tag1);
		res = res.concat(" ");
		res = res.concat(s1);
		res = res.concat(" ");
		res = res.concat(tag2);
		res = res.concat(" ");
		res = res.concat(s2);
		res = res.concat(" ");
		res = res.concat(ready);
		return res;
	}

	private void mynextfunc(){
		Update_the_pc();
		Instruction_Fetch(stagepc.get(0));
		Instruction_Decode(stagepc.get(1));
		Read(stagepc.get(2));
		Reservation_Station(stagepc.get(2));
		Complete(stagepc.get(4));
		Retire(stagepc.get(5));
	}
	
	
	private void myfunc(){
		
		for(int i=0;i<entrysize;i++){
			busyarray.add(0);
			busylabelmaker.get(i).setText(" 0 ");
		}
		
		for(int i=0;i<6;i++){
			stagepc.add(-1);
		}
		pc=0;
		btnStart.setEnabled(false);
		btnNext.setEnabled(true);
		mynextfunc();
	} 
	
	
	
	private void Update_the_pc (){
		//if regstation is full wait else send
		if(RSfreeSlots>instpercycle)
			stall=0;
		
		
		if(stall==0){
			stagepc.remove(stagepc.size()-1);
			if(pc<InstructionCount){
				stagepc.add(0,pc);
				pc=pc+instpercycle;
			}
			else{
				stagepc.add(0,-1);
			}	
		}
		else{
			
		}
		
		System.out.println(stagepc);
		System.out.println("free slots =========>"+RSfreeSlots);
	}
	
	private String GetRegNumbers (String inst){
		String result="";
		String[] array = inst.split(" ");
		String temp1,temp2,temp3;
		if(array[0].equals("ADD")||array[0].equals("SUB")||array[0].equals("MUL")){
			
			temp1=array[1].split("R")[1];
			temp2=array[2].split("R")[1];	
			if(array[3].charAt(0)=='R')
				temp3=array[3].split("R")[1];
			else
				temp3="&"+array[3].split("R")[0];
			result=temp1+" "+temp2+" "+temp3;
		}else if(array[0].equals("LD")){
			temp1=array[1].split("R")[1];
			if(array[2].charAt(0)=='['){
				temp2=array[2].substring(2).split("]")[0];
			}else{
				temp2 = "&"+array[2];
			}	
			temp3="-1";
			result=temp1+" "+temp2+" "+temp3;
		}else if(array[0].equals("SD")){
			temp1=array[1].substring(2).split("]")[0];	
			temp2=array[2].split("R")[1];
			temp3="-1";
			result=temp1+" "+temp2+" "+temp3;
		}else if(array[0].equals("JMP")){
			if(array[1].charAt(0)=='R')
				temp1=array[1].split("R")[1];
			else
				temp1="&"+array[1].split("R")[0];
			temp2="-1";
			temp3="-1";
			result=temp1+" "+temp2+" "+temp3;
		}else if(array[0].equals("BEQZ")){
			if(array[1].charAt(0)=='R')
				temp1=array[1].split("R")[1];
			else
				temp1="&"+array[1].split("R")[0];
			temp2="&"+array[2].split("R")[0];
			temp3="-1";
			result=temp1+" "+temp2+" "+temp3;
		}else if(array[0].equals("HLT")){
			result="-1 -1 -1";
		}
		
		return result;
	} 
	
	
	
	private void Instruction_Fetch(int curpc){		
		String temp="";
		if(curpc!=-1){			
			for(int i=0;i<instpercycle;i++){
				String insttemp="";			
				if (curpc+i < InstructionCount )
					insttemp=InstructionSet.get(curpc+i);
				
				if(i!=0)
					temp=temp+"    "+insttemp;
				else{
					temp=temp+insttemp;
				}
			}
		}
		System.out.println(temp);
		lblIftxt.setText(temp);
	}
	private void Instruction_Decode(int curpc){
		String temp="";
		if(curpc!=-1){			
			for(int i=0;i<instpercycle;i++){
				String insttemp="";			
				if (curpc+i < InstructionCount )
					insttemp=DecodeInstSet.get(curpc+i);
				
				if(i!=0)
					temp=temp+"    "+insttemp;
				else{
					temp=temp+insttemp;
				}
			}
		}
		System.out.println(temp);
		lblIdtxt.setText(temp);
	}

	
	private void Read(int curpc){
		System.out.println(pc);
		if(curpc==-1){
			lblRdtxt.setText("");
		}else{
			String temp="";
			for(int i=0;i<instpercycle;i++){
				String insttemp="";			
				if (curpc+i < InstructionCount )
					insttemp=DecodeInstSet.get(curpc+i);
				
				if(i!=0)
					temp=temp+"    "+insttemp;
				else{
					temp=temp+insttemp;
				}
			}
			if(RSfreeSlots>=instpercycle){
				lblRdtxt.setText("Dispatching "+temp);
			}else{
				stall=1;
				int numb=instpercycle-RSfreeSlots;
				String[] array = temp.split("    ");
				String newtemp="";
			    for(int i=0;i<RSfreeSlots;i++){					
					if(i!=0)
						newtemp=newtemp+"    "+array[i];
					else{
						newtemp=newtemp+array[i];
					}			    	
			    }
			    lblRdtxt.setText("Buffer:"+temp+"Dispatching "+newtemp);    
			    
			    
			   
			}
			
		}
	}
	private int getnumberop(String opc){
		String s1 = "ADD";
		String s2 = "SUB";
		String s3 = "MUL";
		String s4 = "LD";
		String s5 = "SD";
		String s6 = "JMP";
		String s7 = "BEQZ";
		String s8 = "HALT";
		if (opc.equals(s1)){
			return 0;
		}
		else if(opc.equals(s2)){
			return 1;
		}
		else if(opc.equals(s3)){
			return 2;
		}
		else if(opc.equals(s4)){
			return 3;
		}
		else if(opc.equals(s5)){
			return 4;
		}
		else if(opc.equals(s6)){
			return 5;
		}
		else if(opc.equals(s7)){
			return 6;
		}
		else if(opc.equals(s8)){
			return 7;
		}
		else{
			return -1;
		}
	}
	private void Reservation_Station(int curpc){
		System.out.println("now");
		int tempfreeslot = RSfreeSlots;
		int count = 0;

		if(curpc==-1){}
		else{
			int mynumb;
			
			if(RSfreeSlots>instpercycle){
				System.out.println("shir");
				mynumb=instpercycle;
			}else{
				mynumb=RSfreeSlots;
			}
			System.out.println(RSfreeSlots);
			ArrayList<String> smalllist = new ArrayList<String>();
			System.out.println("-------------------------------"+DecodeInstSet.get(4));
			for(int i=0;i<mynumb;i++){
				smalllist.add(DecodeInstSet.get(curpc+i));
				count = count + 1;
				System.out.println(i);
			}
			System.out.println("****************");
			System.out.println(smalllist);
			System.out.println(curpc);
			for (int i = 0; i < count; i++) {
				System.out.println(smalllist.get(i));
				String s = smalllist.get(i);
				String[] parts = s.split(" ");
				//System.out.println(parts[0]);
				int opcode = getnumberop(parts[0]);
				//System.out.println( getnumberop(parts[0]));
				System.out.println("00000000000000");
				System.out.println(GetRegNumbers(s));
				//reserveinst.set(curpc+i, stringrs("1","-1","-1","-1","-1","-1","-1","0"));
			}
			System.out.println("****************");
			for(int i=0;i<entrysize;i++){
				if(smalllist.size()>0){
					if(busyarray.get(i)==0){
						reslabelmaker.get(i).setText(smalllist.get(0));
						smalllist.remove(0);
						busyarray.set(i, 1);
						busylabelmaker.get(i).setText("   1");
					}
				}
			}
			System.out.println(busyarray);
			
			int simpletemp=0;
			for(int i=0;i<busyarray.size();i++){
				if(busyarray.get(i)== 0){
					simpletemp++;
				}
			}
			RSfreeSlots=simpletemp;
			
			if(stagepc.get(0)>0&& stall==1){
				if(stagepc.get(0)+tempfreeslot>=InstructionCount)
					stagepc.set(0, -1);	
				else
					stagepc.set(0, stagepc.get(0)+tempfreeslot);	
			}
		    if(stagepc.get(1)>0&& stall==1){
		    	if(stagepc.get(1)+tempfreeslot>=InstructionCount)
					stagepc.set(0, -1);	
				else
					stagepc.set(1, stagepc.get(1)+tempfreeslot);
		    }
		    	
		    
		    if(stagepc.get(2)>0&& stall==1){
		        if(stagepc.get(2)+tempfreeslot>=InstructionCount)
					stagepc.set(0, -1);	
				else
					stagepc.set(2, stagepc.get(2)+tempfreeslot);
		    }
		    	
			

		    
		   System.out.println(stagepc);
	
		}
	}
	private void Complete(int pc){}
	private void Retire(int pc){}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(119, 136, 153));
		frame.setBounds(100, 100, 1350, 725);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setVisible(true);
		
		
		 btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					myfunc();
				}
			});
			btnStart.setBounds(732, 668, 87, 25);
			frame.getContentPane().add(btnStart);
			
			 btnNext = new JButton("Next");
			 btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					mynextfunc();
					/*if(!sixqueue.isEmpty())
					mynextfunc();
					else{
						btnNext.setEnabled(false);
					}*/
				}
			});
			 btnNext.setEnabled(false);
			 btnNext.setBounds(652, 668, 87, 25);
			frame.getContentPane().add(btnNext);
			
			
			 btnQuit = new JButton("Exit");
			 btnQuit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					frame.dispose();
				}
			});
			 btnQuit.setEnabled(false);
			 btnQuit.setBounds(567, 668, 87, 25);
			frame.getContentPane().add(btnQuit);
		
		JLabel lblHead= new JLabel("Superscalar Pipelined Processor");
		lblHead.setFont(new Font("Abyssinica SIL", Font.BOLD, 28));
		lblHead.setBounds(500, 30, 600, 40);
		frame.getContentPane().add(lblHead);
		
		JLabel lblIf = new JLabel("Instruction Fetch");
		lblIf.setBounds(200, 100, 200, 15);
		frame.getContentPane().add(lblIf);
		
		JLabel lblId= new JLabel("Instructon Decode");
		lblId.setBounds(200, 150, 200, 15);
		frame.getContentPane().add(lblId);
		
		JLabel lblRd= new JLabel("Read/Dispatch");
		lblRd.setBounds(200, 200, 200, 15);
		frame.getContentPane().add(lblRd);
	
		lblIftxt = new JLabel("");
		lblIftxt.setOpaque(true);
		lblIftxt.setBackground(Color.white);
		lblIftxt.setBounds(365, 95, 774, 25);
		frame.getContentPane().add(lblIftxt);
		
		lblIdtxt = new JLabel("");
		lblIdtxt.setOpaque(true);
		lblIdtxt.setBackground(Color.white);
		lblIdtxt.setBounds(365, 145, 774, 25);
		frame.getContentPane().add(lblIdtxt);
		
		lblRdtxt = new JLabel("");
		lblRdtxt.setOpaque(true);
		lblRdtxt.setBackground(Color.white);
		lblRdtxt.setBounds(365, 195, 774, 25);
		frame.getContentPane().add(lblRdtxt);
			
		int xcord=550;
		int ycord=275;
		int curx= 330;
		int cury=310;
	
		for(int i=0;i<entrysize;i++){
			
			JLabel lblbusytxt = new JLabel("");
			lblbusytxt.setOpaque(true);
			lblbusytxt.setBackground(Color.white);
			lblbusytxt.setBounds(xcord, ycord, 50, 25);
			frame.getContentPane().add(lblbusytxt);
			
			JLabel lblrestxt = new JLabel("");
			lblrestxt.setOpaque(true);
			lblrestxt.setBackground(Color.white);
			lblrestxt.setBounds(xcord+53, ycord, 200, 25);
			frame.getContentPane().add(lblrestxt);
			
			JLabel lblvaltxt = new JLabel("");
			lblvaltxt.setOpaque(true);
			lblvaltxt.setBackground(Color.white);
			lblvaltxt.setBounds(xcord+256, ycord, 50, 25);
			frame.getContentPane().add(lblvaltxt);
			
			validlabelmaker.add(i, lblvaltxt);
			reslabelmaker.add(i, lblrestxt);
			busylabelmaker.add(i, lblbusytxt);
			
			ycord=ycord+30;
		}
		
	
		if (ycord<450)
			ycord=450;
		else{
			ycord=ycord+10;
		}
		
		
		JLabel lblMem = new JLabel("Complete");
		lblMem.setBounds(200, ycord+5, 200, 15);
		frame.getContentPane().add(lblMem);
		
		JLabel lblWb= new JLabel("Retire");
		lblWb.setBounds(200, ycord+55, 200, 15);
		frame.getContentPane().add(lblWb);
		
		lblcompletetxt = new JLabel("");
		lblcompletetxt.setOpaque(true);
		lblcompletetxt.setBackground(Color.white);
		lblcompletetxt.setBounds(365, ycord, 774, 25);
		frame.getContentPane().add(lblcompletetxt);
		
		lblretiretxt = new JLabel("");
		lblretiretxt.setOpaque(true);
		lblretiretxt.setBackground(Color.white);
		lblretiretxt.setBounds(365, ycord+50, 774, 25);
		frame.getContentPane().add(lblretiretxt);
		
		lblReservationStation = new JLabel("Reservation Station");
		lblReservationStation.setFont(new Font("Dialog", Font.BOLD, 14));
		lblReservationStation.setBounds(625, 176, 200, 50);
		frame.getContentPane().add(lblReservationStation);
			
		
	}

	private static class __Tmp {
		private static javax.swing.JTextField textField;
		private static void __tmp() {
			  javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
			  
			  textField = new javax.swing.JTextField();
			  __wbp_panel.add(textField);
			  textField.setColumns(10);
		}
	}
}
