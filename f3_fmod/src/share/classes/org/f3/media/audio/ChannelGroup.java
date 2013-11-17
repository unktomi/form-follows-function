package org.f3.media.audio;
import java.util.List;

public interface ChannelGroup extends ChannelNode {
    public Channel[] getChannels();
    public ChannelGroup[] getChannelGroups();

    public void addChannelGroup(ChannelGroup channelGroup);
    public void removeChannelGroup(ChannelGroup channelGroup);

    public void addChannel(Channel channel);
    public void removeChannel(Channel channel);

    public DSP getDSPHead();

    public String getName();
}
