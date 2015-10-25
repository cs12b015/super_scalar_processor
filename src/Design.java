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
import java.math.BigInteger;


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
	private int al;
	private int ll;
	private int ml;
	private int sl;
	private int bl;
	private int a;
	private int m;
	private int s;
	private int b;
	private int l;
	
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
	private ArrayList<JLabel> registerlabelmaker = new ArrayList<JLabel> ();
	
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
	private ArrayList<String> Inslist = new ArrayList<String>();
	private JLabel lblAdder;
	private JLabel lblAddertxt;
	private JLabel lblMULtxt;
	private JLabel lblLoadtxt;
	private JLabel lblAddercounttxt;
	private JLabel lblMULcounttxt;
	private JLabel lblLoadcounttxt;
	private JLabel lblStorecounttxt;
	private JLabel lblBranchcounttxt;
	private JLabel lblStoretxt;
	private JLabel lblBranchtxt;
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
		al = addlatency;
		ml = mullatency;
		ll = loadlatency;
		sl = storelatency;
		bl = branchlatency;
		a = 0;
		m = 0;
		l = 0;
		s = 0;
		b = 0;
		
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
		Execute();
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
	private void set_archregisters(int opcode,int destr,String dest1,String dest2){
		String s = ArchReg.get(Integer.parseInt(dest1));
		String parts[] = s.split(" ");
		if((opcode == 0)||(opcode == 1)||(opcode == 2)||(opcode == 3)){
			ArchReg.set(Integer.parseInt(dest1),stringarf("1",Integer.toString(destr),parts[2]));
		}
		else{}
	}
	private int check_ifBusy(String s){
		String parts[] = s.split(" ");
		if(parts[0].equals("1")){
			return 1;
		}
		else{
			return 0;
		}
	}
	private String get_tag(String s){
		String parts[] = s.split(" ");
		return parts[1];
	}
	private String get_data(String s){
		String parts[] = s.split(" ");
		return parts[2];
	}
	private void AddtoInslist(){
		for (int i = 0; i < reserveinst.size(); i++) {
			String parts[] = reserveinst.get(i).split(" ");
			if(parts[parts.length-1].equals("1")){
				Inslist.add(i+" "+reserveinst.get(i));
			}
		}
	}
	private void set_reservstation(int opcode,String instnumb,int index,String s0,String s1,String s2){
		if((opcode == 0)||(opcode == 1)||(opcode == 2)){
			if(s2.substring(0,1).equals("&")){
				String s = ArchReg.get(Integer.parseInt(s1));
				int flag = check_ifBusy(s);
				if(flag == 1){
					String c = get_tag(s);
					reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c,"&","-1",s2.substring(1),"0"));
				}
				else{
					String c = get_data(s);
					reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c,"-1",s2.substring(1),"1"));
				}
			}
			else{
				String S1 = ArchReg.get(Integer.parseInt(s1));
				String S2 = ArchReg.get(Integer.parseInt(s2));
				int flag1 = check_ifBusy(S1);
				int flag2 = check_ifBusy(S2);
				if((flag1 == 1)&&(flag2 == 0)){
					String c1 = get_tag(S1);
					String c2 = get_data(S2);
					reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c1,"&","-1",c2,"0"));
				}
				else if((flag1 == 0)&&(flag2 == 1)){
					String c1 = get_data(S1);
					String c2 = get_tag(S2);
					reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c1,c2,"&","0"));
				}
				else if((flag1 == 1)&&(flag2 == 1)){
					String c1 = get_tag(S1);
					String c2 = get_tag(S2);
					reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c1,"&",c2,"&","0"));
				}
				else if((flag1 == 0)&&(flag2 == 0)){
					String c1 = get_data(S1);
					String c2 = get_data(S2);
					reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c1,"-1",c2,"1"));
				}
			}
		}
		else if(opcode == 3){
			String s = ArchReg.get(Integer.parseInt(s1));
			int flag = check_ifBusy(s);
			if(flag == 1){
				String c = get_tag(s);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c,"&","-1","&","0"));
			}
			else{
				String c = get_data(s);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c,"-1","&","1"));
			}
		}
		else if(opcode == 4){
			String S0 = ArchReg.get(Integer.parseInt(s0));
			String S1 = ArchReg.get(Integer.parseInt(s1));
			int flag0 = check_ifBusy(S0);
			int flag1 = check_ifBusy(S1);
			if((flag0 == 1)&&(flag1 == 0)){
				String c0 = get_tag(S0);
				String c1 = get_data(S1);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c0,"&","-1",c1,"0"));
			}
			else if((flag0 == 0)&&(flag1 == 1)){
				String c0 = get_data(S0);
				String c1 = get_tag(S1);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c0,c1,"&","0"));
			}
			else if((flag0 == 1)&&(flag1 == 1)){
				String c0 = get_tag(S0);
				String c1 = get_tag(S1);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c0,"&",c1,"&","0"));
			}
			else if((flag0 == 0)&&(flag1 == 0)){
				String c0 = get_data(S0);
				String c1 = get_data(S1);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c0,"-1",c1,"1"));
			}
		}
		else if(opcode == 5){
			reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",s0.substring(1),"-1","-1","1"));
		}
		else if(opcode == 6){
			String s = ArchReg.get(Integer.parseInt(s0));
			int flag = check_ifBusy(s);
			if(flag == 1){
				String c = get_tag(s);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&",c,"&","-1",s1.substring(1),"0"));
			}
			else{
				String c = get_data(s);
				reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"&","-1",c,"-1",s1.substring(1),"1"));
			}
		}
		else {
			reserveinst.set(index,stringrs("1",instnumb+"->"+Integer.toString(opcode),"-1","-1","-1","-1","1","1"));
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
				int temp=curpc+i;
				/*smalllist.add(temp+" "+DecodeInstSet.get(curpc+i));*/
				smalllist.add(DecodeInstSet.get(curpc+i)+" "+temp);
				count = count + 1;
				System.out.println(i);
			}
			System.out.println("****************");
			System.out.println(smalllist);
			System.out.println(curpc);
			for (int i = 0; i < count; i++) {
				System.out.println(smalllist.get(i));
				String s = smalllist.get(i);
				String[] parts1 = s.split(" ");
				//System.out.println(parts[0]);
				int opcode = getnumberop(parts1[0]);
				//System.out.println( getnumberop(parts[0]));
				System.out.println("00000000000000");
				System.out.println(GetRegNumbers(s));
				String[] parts2 = GetRegNumbers(s).split(" ");
				System.out.println(parts2[0] + " " + parts2[1] + " " + parts2[2]);
				set_reservstation(opcode,parts1[parts1.length-1],curpc+i,parts2[0],parts2[1],parts2[2]);
				set_archregisters(opcode,curpc+i,parts2[0],parts2[1]);
				System.out.println("Changed Arch registers");
				for (int j = 0; j < 8; j++) {
					System.out.println(ArchReg.get(j));
				}
				System.out.println("((((((((((((()))))))))))))");
				System.out.println(Inslist.isEmpty());
				
			
				//reserveinst.set(curpc+i, stringrs("1","-1","-1","-1","-1","-1","-1","0"));
			}
			AddtoInslist();
			for(int i=0;i<8;i++){
				String tempo =ArchReg.get(i);
				String[] tempoarray = tempo.split(" ");
				String finalstring = "        "+tempoarray[0]+"        "+tempoarray[1]+"        "+tempoarray[2];
				registerlabelmaker.get(i).setText(finalstring);
			}
			
			
			
			for (int j = 0; j < reserveinst.size(); j++) {
				String tempstring = reserveinst.get(j);
				String[] array = tempstring.split(" ");
				String first = array[0];
				String last = array[7];
				String mid = "   "+array[1]+"   "+ array[2]+"   "+ array[3]+"   "+ array[4]+"   "+ array[5]+"   "+ array[6];
				reslabelmaker.get(j).setText(mid);
				validlabelmaker.get(j).setText(last);
				busyarray.set(j,Integer.parseInt(first));
				busylabelmaker.get(j).setText("   "+first);
				
				System.out.println(reserveinst.get(j));
			}

			
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
	

	
	private void Execute(){
		if(!Inslist.isEmpty()){
			HashMap <Integer,String> localmap = new HashMap<Integer,String>();
			for(int i=0;i<Inslist.size();i++){
				String tempp = Inslist.get(i);
				String[] tempparray = tempp.split(" ");
				String[] arrowsplit = tempparray[2].split("->");
				int key = Integer.parseInt(arrowsplit[0]);
				String value = tempparray[0]+" "+arrowsplit[1]+" "+tempparray[3]+" "+tempparray[4]+" "+tempparray[5]+" "+tempparray[6]+" "+tempparray[7];
				localmap.put(key, value);
			}
			System.out.println(localmap);
			Map<Integer, String> sortedMap = new TreeMap<Integer, String>(localmap);
			for(Integer key : sortedMap.keySet()){
				String s = sortedMap.get(key);
				executeop(s);
			}
			//System.out.println(sortedMap.size());	
		}
	}
	private void executeop(String s){
		String parts[] = s.split(" ");
		if((parts[1].equals("0"))&&(a == 0)){
			if(al > 0){
				al = al -1;
				if(al == 0){
					int temp1 = Integer.parseInt(parts[4]);
					int temp2 = Integer.parseInt(parts[6]);
					int result = temp1 + temp2;
					set_reservstation(0, instnumb, index, s0, s1, s2)
					a = 0;
					al = addlatency;
				}
			}
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
		
		/*JLabel lblHead= new JLabel("Superscalar Pipelined Processor");
		lblHead.setFont(new Font("Abyssinica SIL", Font.BOLD, 28));
		lblHead.setBounds(430, 30, 600, 40);
		frame.getContentPane().add(lblHead);*/
		
		JLabel lblIf = new JLabel("Instruction Fetch");
		lblIf.setBounds(50, 100, 200, 15);
		frame.getContentPane().add(lblIf);
		
		JLabel lblId= new JLabel("Instructon Decode");
		lblId.setBounds(50, 150, 200, 15);
		frame.getContentPane().add(lblId);
		
		JLabel lblRd= new JLabel("Read/Dispatch");
		lblRd.setBounds(50, 200, 200, 15);
		frame.getContentPane().add(lblRd);
	
		lblIftxt = new JLabel("");
		lblIftxt.setOpaque(true);
		lblIftxt.setBackground(Color.white);
		lblIftxt.setBounds(215, 95, 774, 25);
		frame.getContentPane().add(lblIftxt);
		
		lblIdtxt = new JLabel("");
		lblIdtxt.setOpaque(true);
		lblIdtxt.setBackground(Color.white);
		lblIdtxt.setBounds(215, 145, 774, 25);
		frame.getContentPane().add(lblIdtxt);
		
		lblRdtxt = new JLabel("");
		lblRdtxt.setOpaque(true);
		lblRdtxt.setBackground(Color.white);
		lblRdtxt.setBounds(215, 195, 774, 25);
		frame.getContentPane().add(lblRdtxt);
			
		int xcord=80;
		int ycord=275;
	
		lblReservationStation = new JLabel("Reservation Station");
		lblReservationStation.setFont(new Font("Dialog", Font.BOLD, 14));
		lblReservationStation.setBounds(xcord+75, ycord-50, 200, 50);
		frame.getContentPane().add(lblReservationStation);
		
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
		
		int regx=1100;
		int regy=180;
		
		JLabel archreg = new JLabel("Arch Registers");
		archreg.setBounds(regx+40,regy-30, 200, 15);
		frame.getContentPane().add(archreg);
		
		
		lblAdder = new JLabel("Adder");
		lblAdder.setBounds(500, 255, 175, 15);
		frame.getContentPane().add(lblAdder);
		
		lblAddertxt = new JLabel("");
		lblAddertxt.setOpaque(true);
		lblAddertxt.setBackground(Color.white);
		lblAddertxt.setBounds(430, 270, 175, 40);
		frame.getContentPane().add(lblAddertxt);
		
		lblAddercounttxt = new JLabel("");
		lblAddercounttxt.setOpaque(true);
		lblAddercounttxt.setBackground(Color.white);
		lblAddercounttxt.setBounds(610, 270,50, 40);
		frame.getContentPane().add(lblAddercounttxt);
		
		JLabel lblMUL = new JLabel("Mul");
		lblMUL.setBounds(500, 345, 175, 15);
		frame.getContentPane().add(lblMUL);
		
		lblMULtxt = new JLabel("");
		lblMULtxt.setOpaque(true);
		lblMULtxt.setBackground(Color.white);
		lblMULtxt.setBounds(430, 360, 175, 40);
		frame.getContentPane().add(lblMULtxt);
		
		lblMULcounttxt = new JLabel("");
		lblMULcounttxt.setOpaque(true);
		lblMULcounttxt.setBackground(Color.white);
		lblMULcounttxt.setBounds(610, 360, 50, 40);
		frame.getContentPane().add(lblMULcounttxt);
		
		JLabel lblLoad = new JLabel("Load");
		lblLoad.setBounds(500, 435, 175, 15);
		frame.getContentPane().add(lblLoad);
		
		lblLoadtxt = new JLabel("");
		lblLoadtxt.setOpaque(true);
		lblLoadtxt.setBackground(Color.white);
		lblLoadtxt.setBounds(430, 450, 175, 40);
		frame.getContentPane().add(lblLoadtxt);
		
		lblLoadcounttxt = new JLabel("");
		lblLoadcounttxt.setOpaque(true);
		lblLoadcounttxt.setBackground(Color.white);
		lblLoadcounttxt.setBounds(610, 450,50, 40);
		frame.getContentPane().add(lblLoadcounttxt);
		
		JLabel lblStore = new JLabel("Store");
		lblStore.setBounds(800, 255, 175, 15);
		frame.getContentPane().add(lblStore);
		
		lblStoretxt = new JLabel("");
		lblStoretxt.setOpaque(true);
		lblStoretxt.setBackground(Color.white);
		lblStoretxt.setBounds(740, 270, 175, 40);
		frame.getContentPane().add(lblStoretxt);
		
		lblStorecounttxt = new JLabel("");
		lblStorecounttxt.setOpaque(true);
		lblStorecounttxt.setBackground(Color.white);
		lblStorecounttxt.setBounds(920, 270, 50, 40);
		frame.getContentPane().add(lblStorecounttxt);
		
		JLabel lblBranch = new JLabel("Store");
		lblBranch.setBounds(800, 345, 175, 15);
		frame.getContentPane().add(lblBranch);
		
		lblBranchtxt = new JLabel("");
		lblBranchtxt.setOpaque(true);
		lblBranchtxt.setBackground(Color.white);
		lblBranchtxt.setBounds(740, 360, 175, 40);
		frame.getContentPane().add(lblBranchtxt);
		
		lblBranchcounttxt = new JLabel("");
		lblBranchcounttxt.setOpaque(true);
		lblBranchcounttxt.setBackground(Color.white);
		lblBranchcounttxt.setBounds(920, 360, 50, 40);
		frame.getContentPane().add(lblBranchcounttxt);
		
		
		
		
		
		
		
		for(int i=0;i<8;i++){
			
			JLabel lblregtxt = new JLabel("");
			lblregtxt.setOpaque(true);
			lblregtxt.setBackground(Color.white);
			lblregtxt.setBounds(regx, regy, 180, 25);
			
			frame.getContentPane().add(lblregtxt);
			registerlabelmaker.add(lblregtxt);
			regy=regy+30;
		}
		
		
		
		
		
		
		JLabel lblMem = new JLabel("Complete");
		lblMem.setBounds(50, ycord+15, 200, 15);
		frame.getContentPane().add(lblMem);
		
		JLabel lblWb= new JLabel("Retire");
		lblWb.setBounds(50, ycord+65, 200, 15);
		frame.getContentPane().add(lblWb);
		
		lblcompletetxt = new JLabel("");
		lblcompletetxt.setOpaque(true);
		lblcompletetxt.setBackground(Color.white);
		lblcompletetxt.setBounds(215, ycord+10, 774, 25);
		frame.getContentPane().add(lblcompletetxt);
		
		lblretiretxt = new JLabel("");
		lblretiretxt.setOpaque(true);
		lblretiretxt.setBackground(Color.white);
		lblretiretxt.setBounds(215, ycord+60, 774, 25);
		frame.getContentPane().add(lblretiretxt);
		
		
		
		
			
		
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
