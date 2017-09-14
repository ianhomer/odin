import { connect } from 'react-redux'

import { deleteChannel } from '../actions'
import { fetchEntities } from '../actions/fetch.js'

import ChannelList from '../components/channelList'


class ChannelsContainer extends Component {
  constructor(props) {
    super(props)
  }

  componentDidMount() {
    dispatch(fetchEntities('channel', this.props.schema))
  }

  render() {
    return (
      <ChannelList schema={this.props.schema} project={this.state.project} flux={this.props.flux}
        entities={this.props.entities}/>
    )
  }
}

function mapStateToProps(state) {
  const entities = state['channels']

  return {
    entities,
  }
}

const mapDispatchToProps = dispatch => {
  return {
    onDeleteChannel: entity => {
      dispatch(deleteChannel(entity))
    }
  }
}

ChannelsContainer.propTypes = {
  dispatch: PropTypes.func.isRequired,
  entities: PropTypes.array.isRequired,
  schema: PropTypes.object.isRequired
}

export default connect(mapStateToProps, mapDispatchToProps)(ChannelsContainer)