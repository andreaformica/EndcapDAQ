/**
 * 
 */
package ec.dim.tools;

/**
 * @author formica
 *
 */
public enum ChannelNameResolver {

	AlB ("CHB"),
	AlD ("CHD"),
	AlR ("CHR"),
	AlT ("CHT");
	
	private String dimExtension;
	
	ChannelNameResolver(String ch) {
		this.dimExtension = ch;
	}
	
	public String dimChannel() {
		return dimExtension;
	}
}
