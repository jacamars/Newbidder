import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.jacamars.dsp.rtb.exchanges.google.GoogleBidRequest;
import com.jacamars.dsp.rtb.exchanges.openx.OpenXWinObject;
import com.jacamars.dsp.rtb.exchanges.openx.SsRtbCrypter;

import javax.crypto.spec.SecretKeySpec;

public class Test {

	public static void main(String args []) throws Exception {

			SsRtbCrypter crypter = new SsRtbCrypter();
			SecretKeySpec encryption = OpenXWinObject.getKeySpec("7EC9482F7636405087F6EC9851ED0E3D376182163B6646F69E9527EB9F45BE12");
			SecretKeySpec integrity = OpenXWinObject.getKeySpec("6E5240C73E4A48FF961E943925BA3C5D9E9810BB918E4FB5B949BE98CED2E9F6");
			long price = 1000;

			System.out.println("Encrypted: " + crypter.encryptEncode(price,encryption,integrity));

			String proto = Charset.defaultCharset()
					.decode(ByteBuffer
							.wrap(Files.readAllBytes(Paths.get("./SampleBids/nositedomain.proto"))))
					.toString();
			
			byte[] data = Base64.getDecoder().decode(proto);
			//byte[] data = DatatypeConverter.parseBase64Binary(proto);
			InputStream is = new ByteArrayInputStream(data);
			GoogleBidRequest r = new GoogleBidRequest(is);
			System.out.println(r.getInternal());
			Object s = r.interrogate("site.domain");
			System.out.print("Domain: ");
			System.out.println(s);
	}
}
