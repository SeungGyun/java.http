package com.ilhsk.http;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*
 * 코로나 초창기 마스크 살수있는지 체크하는 로직
 */
public class MaskBuy implements Runnable {

	
    private final String url;
    public MaskBuy(String url){
        this.url = url;
    }
    public void run(){
    	
    	GetHttp http = new GetHttp();
	
		
		while(true) {
			Date time = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
			try {
				if (http.get(url)) {
					if(http.getString().indexOf("checkoutPurchase")>-1) {
						System.out.println(format1.format(time) + " 상품 사러가자 > "+ url);
						try {
							//TODO 본인이 받을 수있는 시스템 정보 넣으면됨  메신저 전송 모듈 추가했었음
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						
					}else {
						System.out.println(format1.format(time)+" 아직 안열림 -> "+ url);
					}
				}else {
					//TODO HTTP 전송 가져오기 전 에러 나면 여기다가 호출
				}
			
				int sec = ((int)Math.random()* 5) + 10;
				Thread.sleep(1000*sec);
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
		}
        
    }


	public static void main(String[] args) throws Exception {		
		String[] goods = {
				"https://smartstore.naver.com/sangkong/products/4762917002",
				"https://smartstore.naver.com/aer-shop/products/4722827602",
				"https://smartstore.naver.com/gonggami/products/4705579501",
				"https://smartstore.naver.com/mfbshop/products/4072435942",
				"https://smartstore.naver.com/soommask/products/4828127993"};
		ArrayList<MaskBuy> thread = new ArrayList<MaskBuy>();
		for (String url : goods) {
			thread.add(new MaskBuy(url));	
		}
		
		for (MaskBuy maskBuy : thread) {
			 Thread maskThread = new Thread(maskBuy);
			 maskThread.start();
		}
		
	}


}


