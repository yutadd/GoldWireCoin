package priv.key;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;




	/***********************************************************************************************
	 * 												*
	 *		Ellipische Kurven-Operationen Secp256k1 mit Bouncycastle			*
	 *		-	Erstellung einer ECDSA Signatur						*
	 *		-	Verifizierung der ECDSA Signatur					*
	 *		-	EC-Multiplikation und Addition						*
	 *												*
	 ***********************************************************************************************/




public class Bouncycastle_Secp256k1
{






/**	Es wird eine Signatur erstellt bestehend aus den Teilen "r" und "s".
 *	Übergeben wird der 32byte lange Hash, der signiert werden soll,
 *	- der Priv.Key 32Byte,
 *	- die Zufallszahl "k" als ByteArray.
 *	Rückgabe ist ein BigInteger-Array bestehend aus 2 Elementen: [0] = r   und    [1] = s.
 *	Achtung: Die Zufallszahl "k" muss aus einer kryptographisch starken Entropie stammen!
 *	Falls "k" vorhersebar ist, kann der Priv.Key leicht aufgedeckt werden!!! */
public static BigInteger[] sig(byte[] hash, byte[] priv, byte[] k)
{
	k = Secp256k1.to_fixLength(k,32);
	X9ECParameters p = SECNamedCurves.getByName("secp256k1");
 	ECDomainParameters params = new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH());
	ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(new BigInteger(1,priv), params);
	SecureRandom rand = new FixedSecureRandom(k);
	ECDSASigner dsa = new ECDSASigner();
	dsa.init(true, new ParametersWithRandom(priKey, rand));
	BigInteger[] sig = dsa.generateSignature(hash);
	return sig;
}




/**	Die Signatur "r" und "s" wird geprüft.
*	- Übergeben wird der 32byte lange Hash, dessen Signatur geprüft werden soll,
*	- die Signatur selbst "sig" als BigInteger-Array bestehend aus 2 Elementen: [0] = r   und    [1] = s.
*	- und der Pub.Key als BigInteger Array mit 2 Elementen.*/
public static boolean verify(byte[] hash, BigInteger[] sig, BigInteger[] pub)
{
	X9ECParameters p = SECNamedCurves.getByName("secp256k1");
	ECDomainParameters params = new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH());
	ECDSASigner dsa = new ECDSASigner();
	ECPublicKeyParameters pubKey = new ECPublicKeyParameters(params.getCurve().decodePoint(Hex.decode("04" + pub[0].toString(16) + pub[1].toString(16) )), params);
	dsa.init(false, pubKey);
	if (dsa.verifySignature(hash, sig[0], sig[1]))  return true;
	else return false;
}




/** Multipliziert den Generator-Punkt mit dem factor.  */
public static BigInteger[] mul_G(BigInteger factor)
{
	ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
	org.bouncycastle.math.ec.ECPoint Q = spec.getG().multiply(factor).normalize();
	BigInteger[] erg = new BigInteger[2];
	erg[0]=Q.getAffineXCoord().toBigInteger();
	erg[1]=Q.getAffineYCoord().toBigInteger();
	return erg;
}




/** Multipliziert einen Punkt "point" auf der elliptischen Kurve mit dem factor.  */
public static BigInteger[] mul_Point(BigInteger[] point, BigInteger factor)
{
	ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
	ECCurve curve = spec.getCurve();
	org.bouncycastle.math.ec.ECPoint P = curve.createPoint(point[0], point[1]);
	org.bouncycastle.math.ec.ECPoint Q = P.multiply(factor).normalize();
	BigInteger[] erg = new BigInteger[2];
	erg[0]=Q.getAffineXCoord().toBigInteger();
	erg[1]=Q.getAffineYCoord().toBigInteger();
	return erg;
}




/**  Addiert zwei Punkte auf der elliptischen Kurve.  */
public static BigInteger[] add(BigInteger[] a, BigInteger[] b)
{
	ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
	ECCurve curve = spec.getCurve();
	org.bouncycastle.math.ec.ECPoint P = curve.createPoint(a[0], a[1]);
	org.bouncycastle.math.ec.ECPoint Q = curve.createPoint(b[0], b[1]);
	org.bouncycastle.math.ec.ECPoint R = P.add(Q).normalize();
	BigInteger[] erg = new BigInteger[2];
	erg[0]=R.getAffineXCoord().toBigInteger();
	erg[1]=R.getAffineYCoord().toBigInteger();
	return erg;
}
}
