package osm.model;

import lombok.Data;
import osm.model.generated.Node;

@Data
public class TagDb {
    private final long nodeId;
    private final String key;
    private final String value;

    public TagDb(long nodeId, String key, String value) {
        this.nodeId = nodeId;
        this.key = key;
        this.value =value;
    }

    public static TagDb convert(Node.Tag tag, long nodeId) {
        return new TagDb(nodeId, tag.getK(), tag.getV());
    }
}
