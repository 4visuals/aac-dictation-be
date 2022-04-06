package github.visual4.aacweb.dictation.service.codec;

public interface ICodec<T, U> {

	U encode(T plainSource);
	
	T decode (U encodedSource);
}
